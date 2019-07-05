/**
 * gvSIG. Desktop Geographic Information System.
 *
 * Copyright © 2007-2016 gvSIG Association
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 *
 * For any additional information, do not hesitate to contact us
 * at info AT gvsig.com, or visit our website www.gvsig.com.
 */
package org.gvsig.gdal.prov.ogr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.gdal.ogr.DataSource;
import org.gdal.ogr.Feature;
import org.gdal.ogr.FeatureDefn;
import org.gdal.ogr.FieldDefn;
import org.gdal.ogr.GeomFieldDefn;
import org.gdal.ogr.Geometry;
import org.gdal.ogr.Layer;
import org.gdal.ogr.ogr;
import org.gdal.ogr.ogrConstants;

import org.gvsig.fmap.dal.DataStore;
import org.gvsig.fmap.dal.DataStoreParameters;
import org.gvsig.fmap.dal.DataTypes;
import org.gvsig.fmap.dal.FileHelper;
import org.gvsig.fmap.dal.exception.DataException;
import org.gvsig.fmap.dal.exception.InitializeException;
import org.gvsig.fmap.dal.exception.OpenException;
import org.gvsig.fmap.dal.exception.ReadRuntimeException;
import org.gvsig.fmap.dal.feature.EditableFeatureAttributeDescriptor;
import org.gvsig.fmap.dal.feature.FeatureAttributeDescriptor;
import org.gvsig.fmap.dal.feature.FeatureQuery;
import org.gvsig.fmap.dal.feature.FeatureQueryOrder;
import org.gvsig.fmap.dal.feature.FeatureQueryOrder.FeatureQueryOrderMember;
import org.gvsig.fmap.dal.feature.FeatureType;
import org.gvsig.fmap.dal.feature.spi.AbstractFeatureStoreProvider;
import org.gvsig.fmap.dal.feature.spi.DefaultFeatureProvider;
import org.gvsig.fmap.dal.feature.spi.FeatureProvider;
import org.gvsig.fmap.dal.feature.spi.FeatureReferenceProviderServices;
import org.gvsig.fmap.dal.feature.spi.FeatureSetProvider;
import org.gvsig.fmap.dal.feature.spi.FeatureStoreProvider;
import org.gvsig.fmap.dal.resource.ResourceAction;
import org.gvsig.fmap.dal.resource.file.FileResource;
import org.gvsig.fmap.dal.resource.spi.ResourceConsumer;
import org.gvsig.fmap.dal.resource.spi.ResourceProvider;
import org.gvsig.fmap.dal.spi.DataStoreProviderServices;
import org.gvsig.fmap.geom.Geometry.SUBTYPES;
import org.gvsig.fmap.geom.GeometryLocator;
import org.gvsig.fmap.geom.primitive.Envelope;
import org.gvsig.fmap.geom.type.GeometryTypeNotSupportedException;
import org.gvsig.fmap.geom.type.GeometryTypeNotValidException;
import org.gvsig.tools.dynobject.DynObject;
import org.gvsig.tools.evaluator.Evaluator;
import org.gvsig.tools.exception.BaseException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:lmarques@disid.com">Lluis Marques</a>
 *
 */
public class OGRDataStoreProvider extends AbstractFeatureStoreProvider implements
    FeatureStoreProvider, ResourceConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(OGRDataStoreProvider.class);

    /**
     *
     */
    public static final String METADATA_DEFINITION_NAME = "OGRDataStoreProvider";

    /**
     *
     */
    public static final String NAME = "OGRDataStoreProvider";

    /**
     *
     */
    public static final String DESCRIPTION = "OGR provider to open vectorial resources";

    protected DataSource dataSource;

    private Envelope envelope;

    private Layer newLayer;

    protected ResourceProvider resourceProvider;

    private Boolean updateSupport;

    private boolean opened = false;

    protected OGRDataStoreProvider(DataStoreParameters dataParameters,
        DataStoreProviderServices storeServices, DynObject metadata) throws InitializeException {
        super(dataParameters, storeServices, metadata);

        // Set CRS parameter to metadata
        this.setDynValue(DataStore.METADATA_CRS, dataParameters.getDynValue(DataStore.METADATA_CRS));

        getResource().addConsumer(this);

        try {
            this.open();
        } catch (OpenException e) {
            throw new InitializeException(NAME, e);
        }
    }

    protected OGRDataStoreProvider(DataStoreParameters dataParameters,
        DataStoreProviderServices storeServices) throws InitializeException {
        this(dataParameters, storeServices, FileHelper
            .newMetadataContainer(METADATA_DEFINITION_NAME));
    }

    /*
     * Lazy initialization of data source
     */
    protected synchronized DataSource getDataSource() throws OGRUnsupportedFormatException {
        if (this.dataSource == null) {

            // Prioritize connection string over file
            if (StringUtils.isNotBlank(getOGRParameters().getConnectionString())) {

                // Trying to open in update mode
                this.dataSource = ogr.Open(getOGRParameters().getConnectionString(), 1);

                if (this.dataSource == null) {
                    this.dataSource = ogr.Open(getOGRParameters().getConnectionString());
                    updateSupport = false;
                } else {
                    updateSupport = true;
                }

            } else if (getOGRParameters().getFile() != null
                && getOGRParameters().getFile().exists()) {

                // Trying to open in update mode
                this.dataSource = ogr.Open(getOGRParameters().getFile().getAbsolutePath(), 1);

                if (this.dataSource == null) {
                    this.dataSource = ogr.Open(getOGRParameters().getFile().getAbsolutePath());
                    updateSupport = false;
                } else {
                    updateSupport = true;
                }

            } else {
                throw new IllegalStateException(
                    "Invalid parameters. Connection string must not be blank or file must exists");
            }
        }

        if (this.dataSource == null) {

            if (StringUtils.isNotBlank(getOGRParameters().getConnectionString())) {
                throw new OGRUnsupportedFormatException(getOGRParameters().getConnectionString());
            }
        }

        return this.dataSource;
    }

    /*
     * Lazy initialization of update support flag
     */
    private Boolean hasUpdateSupport() throws OGRUnsupportedFormatException {
        if (this.updateSupport == null) {
            getDataSource();
        }
        return this.updateSupport;
    }

    /*
     * Lazy initialization of layer
     */
    protected Layer getLayer() throws OGRUnsupportedFormatException {
        if (this.newLayer == null) {
            this.newLayer = getDataSource().GetLayer(getOGRParameters().getLayerName());
            // this.layer = getDataSource().GetLayer(0);
            if (this.newLayer == null) {
                LOG.warn("Can not get layer with {} name. Get first layer of data source",
                    getOGRParameters().getLayerName());
                this.newLayer = getDataSource().GetLayer(0);
                getOGRParameters().setLayerName(this.newLayer.GetName());
            }
        }
        return this.newLayer;
    }

    /*
     * Lazy envelope initialization
     */
    @Override
    public Envelope getEnvelope() throws DataException {
        open();
        if (this.envelope == null) {
            this.envelope = (Envelope) getResource().execute(new ResourceAction() {

                @Override
                public Object run() throws Exception {
                    Layer layer = getLayer();
                    double[] extent = layer.GetExtent(true);
                    if (extent != null) {
                        return GeometryLocator.getGeometryManager().createEnvelope(extent[0],
                            extent[2], extent[1], extent[3], SUBTYPES.GEOM2D);
                    } else {
                        Envelope tmpEnvelope =
                            GeometryLocator.getGeometryManager().createEnvelope(SUBTYPES.GEOM2D);
                        FeatureType featureType = getStoreServices().getDefaultFeatureType();
                        layer.ResetReading();
                        Feature feature = layer.GetNextFeature();
                        while (feature!=null) {
                            double[] envelope = new double[4];
                            int geomFieldIndex =
                                layer.GetLayerDefn().GetGeomFieldIndex(
                                    featureType.getDefaultGeometryAttributeName());
                            Geometry ogrGeometry = feature.GetGeomFieldRef(geomFieldIndex);
                            ogrGeometry.GetEnvelope(envelope);
                            tmpEnvelope.add(GeometryLocator.getGeometryManager()
                                .createEnvelope(envelope[0], envelope[2], envelope[1], envelope[3],
                                    SUBTYPES.GEOM2D));
                            feature = layer.GetNextFeature();
                        }

                        return tmpEnvelope;
                    }
                }
            });
        }
        return this.envelope;
    }

    @Override
    public String getFullName() {

        StringBuilder stb = new StringBuilder();
        stb.append(NAME);
        stb.append(":");
        if (StringUtils.isBlank(getOGRParameters().getConnectionString())) {
            stb.append(getOGRParameters().getFile().getAbsolutePath());
            stb.append(":");
            stb.append(getOGRParameters().getLayerName());
        } else {
            stb.append(getOGRParameters().getConnectionString());
        }
        return stb.toString();
    }

    @Override
    public String getName() {
        return getOGRParameters().getLayerName();
    }

    @Override
    public String getProviderName() {
        return NAME;
    }

    @Override
    public boolean allowWrite() {
        try {
            return getLayer().TestCapability(ogrConstants.OLCAlterFieldDefn)
                && getLayer().TestCapability(ogrConstants.OLCCreateField)
                && getLayer().TestCapability(ogrConstants.OLCDeleteField)
                && getLayer().TestCapability(ogrConstants.OLCDeleteFeature) && hasUpdateSupport();
        } catch (OGRUnsupportedFormatException e) {
            LOG.error("Can not determinate if data source allows write", e);
            return false;
        }
    }

    @Override
    public ResourceProvider getResource() {

        if (this.resourceProvider == null) {
            if (StringUtils.isBlank(getOGRParameters().getConnectionString())) {
                try {
                    this.resourceProvider =
                        this.createResource(FileResource.NAME, new Object[] { getOGRParameters()
                            .getFile().getAbsolutePath() });
                } catch (InitializeException e) {
                    throw new ReadRuntimeException(String.format(
                        "Can not create file resource with %1s path", getOGRParameters().getFile()
                            .getAbsolutePath()), e);
                }
            } else {
                try {
                    this.resourceProvider =
                        this.createResource(OGRResource.NAME, new Object[] { getOGRParameters()
                            .getConnectionString() });
                } catch (InitializeException e) {
                    throw new ReadRuntimeException(String.format(
                        "Can not create OGR resource with %1s", getOGRParameters()
                            .getConnectionString()), e);
                }
            }
        }

        return resourceProvider;
    }

    @Override
    public Object getSourceId() {
        return this.getOGRParameters().getFile();
    }

    @Override
    public void open() throws OpenException {

        if (opened == false) {
            try {
                this.opened = loadFeatureType();
            } catch (BaseException e) {
                LOG.error("Can not load feature type", e);
                throw new OpenException(getFullName(), e);
            }
        }
    }

    protected boolean loadFeatureType() throws OGRUnsupportedFormatException,
        GeometryTypeNotSupportedException, GeometryTypeNotValidException {

        return (boolean) getResource().execute(new ResourceAction() {

            @Override
            public Object run() throws Exception {
                FeatureDefn featureDefn = getLayer().GetLayerDefn();
                OGRConverter converter = new OGRConverter();
                String defaultGeometryField = getOGRParameters().getDefaultGeometryField();
                FeatureType featureType = converter.convert(featureDefn, defaultGeometryField);

                if (featureType.getDefaultSRS() != null) {
                    setDynValue(DataStore.METADATA_CRS, featureType.getDefaultSRS());
                }

                List<FeatureType> featureTypes = new ArrayList<FeatureType>();
                featureTypes.add(featureType);

                getStoreServices().setFeatureTypes(featureTypes, featureType);
                return true;
            }
        });
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void performChanges(final Iterator deleteds, final Iterator inserteds,
        final Iterator updateds, final Iterator featureTypesChanged) throws DataException {

        getResource().execute(new ResourceAction() {

            @Override
            public Object run() throws Exception {
                OGRConverter converter = new OGRConverter();

                if (getLayer().TestCapability(ogrConstants.OLCTransactions)) {
                    getLayer().StartTransaction();
                }

                while (featureTypesChanged.hasNext()) {
                    FeatureTypeChanged featureTypeChange =
                        (FeatureTypeChanged) featureTypesChanged.next();
                    FeatureType source = featureTypeChange.getSource();
                    FeatureType target = featureTypeChange.getTarget();

                    for (int i = 0; i < source.getAttributeDescriptors().length; i++) {
                        EditableFeatureAttributeDescriptor eAttDescriptor =
                            source.getEditable().getEditableAttributeDescriptor(i);

                        if (eAttDescriptor.getOriginalName() != null) {
                            int index =
                                getLayer().GetLayerDefn().GetFieldIndex(
                                    eAttDescriptor.getOriginalName());

                            FieldDefn field = converter.convertField(eAttDescriptor);
                            getLayer().AlterFieldDefn(index, field, ogrConstants.ALTER_ALL_FLAG);
                        } else if (target.getAttributeDescriptor(eAttDescriptor.getName()) == null) {
                            int index = getLayer().FindFieldIndex(eAttDescriptor.getName(), 1);
                            getLayer().DeleteField(index);
                        }
                    }

                    List<FieldDefn> fields = converter.convertFields(target);
                    for (FieldDefn fieldDefn : fields) {
                        int index = getLayer().GetLayerDefn().GetFieldIndex(fieldDefn.GetName());
                        if (index == -1) {
                            getLayer().CreateField(fieldDefn);
                        } else {
                            getLayer()
                                .AlterFieldDefn(index, fieldDefn, ogrConstants.ALTER_ALL_FLAG);
                        }
                    }

                    if (getLayer().TestCapability(ogrConstants.OLCCreateGeomField)) {
                        List<GeomFieldDefn> geometryFields =
                            converter.convertGeometryFields(target, true);
                        for (GeomFieldDefn geomFieldDefn : geometryFields) {
                            int index =
                                getLayer().GetLayerDefn()
                                    .GetGeomFieldIndex(geomFieldDefn.GetName());
                            if (index == -1) {
                                getLayer().CreateGeomField(geomFieldDefn);
                            }
                        }
                    } else {
                        StringBuilder stb = new StringBuilder();
                        stb.append("Driver '");
                        stb.append(getDataSource().GetDriver().GetName());
                        stb.append("' does not support create geometry fields");
                        LOG.warn(stb.toString());
                    }
                }

                while (deleteds.hasNext()) {
                    FeatureReferenceProviderServices reference =
                        (FeatureReferenceProviderServices) deleteds.next();
                    getLayer().DeleteFeature((int) reference.getOID());
                }

                while (inserteds.hasNext()) {
                    FeatureProvider featureProvider = (FeatureProvider) inserteds.next();
                    getLayer().CreateFeature(converter.convert(featureProvider));
                }

                while (updateds.hasNext()) {
                    FeatureProvider featureProvider = (FeatureProvider) updateds.next();
                    Feature ogrFeature = converter.convert(featureProvider);
                    getLayer().SetFeature(ogrFeature);
                }

                if (getLayer().TestCapability(ogrConstants.OLCTransactions)) {
                    getLayer().CommitTransaction();
                }
                getDataSource().SyncToDisk();
                repack();
                getResource().notifyChanges();

                return null;
            }
        });
    }

    protected void repack() throws OGRUnsupportedFormatException {
        LOG.debug("Running SQL: REPACK ".concat(getLayer().GetName()));
        getDataSource().ExecuteSQL("REPACK ".concat(getLayer().GetName()));
    }

    @Override
    public Object createNewOID() {
        try {
            return getFeatureCount() + 1;
        } catch (DataException e) {
            LOG.error("Can't get feature count", e);
            throw new ReadRuntimeException(getFullName(), e);
        }
    }

    @Override
    public FeatureSetProvider createSet(FeatureQuery query, FeatureType featureType)
        throws DataException {
        open();
        return new OGRFetureSetProvider(this, query, featureType);
    }

    @Override
    public long getFeatureCount() throws DataException {
        open();
        return ((Number) getResource().execute(new ResourceAction() {

            @Override
            public Object run() throws Exception {
                int featureCount = getLayer().GetFeatureCount(0);
                if (featureCount == -1) {
                	featureCount = getLayer().GetFeatureCount();
                }
				return featureCount;
            }
        })).longValue();
    }

    @Override
    public int getOIDType() {
        return DataTypes.LONG;
    }

    @Override
    protected FeatureProvider internalGetFeatureProviderByReference(
        FeatureReferenceProviderServices providerServices, FeatureType featureType)
        throws DataException {

        int oid = (int)providerServices.getOID();
        // Parece que hay un bug en el proveedor de SQLite para gdal.
        // Cuando se lee la capa, el método GetFID está indexado empezando por 0,
        // pero cuando se busca una ogrFeature a partir de dicho FID
        // el método GetFeature(fid) está indexado empezando por 1.
        // Esto es para rodear el problema.
        if(this.dataSource.GetDriver().getName().equalsIgnoreCase("SQLite")){
            oid++;
        }
        Feature ogrFeature = getLayer().GetFeature(oid);
        int fid = ogrFeature.GetFID();
        FeatureProvider featureProvider =
            new DefaultFeatureProvider(featureType, fid);
        OGRConverter converter = new OGRConverter();
        featureProvider = converter.convert(featureProvider, featureType, ogrFeature);
        return featureProvider;
    }

    private OGRDataStoreParameters getOGRParameters() {
        return (OGRDataStoreParameters) this.getParameters();
    }

    @SuppressWarnings("rawtypes")
    protected String compoundSelect(FeatureType type, Evaluator evaluator,
        FeatureQueryOrder featureQueryOrder) {

        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        FeatureAttributeDescriptor[] attributeDescriptors = type.getAttributeDescriptors();
        for (int i = 0; i < attributeDescriptors.length; i++) {
        	query.append("\"");
            query.append(attributeDescriptors[i].getName());
            query.append("\"");
            // Don't add the last comma
            if (i < attributeDescriptors.length - 1) {
                query.append(",");
            }
        }

        query.append(" FROM ");
        query.append("\"");
        query.append(getOGRParameters().getLayerName());
        query.append("\"");

        if (featureQueryOrder != null && featureQueryOrder.iterator().hasNext()) {
            query.append(" ORDER BY ");
            Iterator iterator = featureQueryOrder.iterator();
            while (iterator.hasNext()) {
                FeatureQueryOrderMember member = (FeatureQueryOrderMember) iterator.next();

                if (member.hasEvaluator()) {
                    // TODO
                } else {
                    query.append(member.getAttributeName());
                }
                if (member.getAscending()) {
                    query.append(" ASC");
                } else {
                    query.append(" DESC");
                }
                if (iterator.hasNext()) {
                    query.append(", ");
                } else {
                    query.append(' ');
                    break;
                }
            }
        }

        return query.toString();
    }

    @Override
    protected void doDispose() throws BaseException {
        super.doDispose();
        getResource().removeConsumer(this);
        this.resourceProvider = null;
        getDataSource().delete();
        this.envelope = null;
        this.newLayer = null;
        this.dataSource = null;
        this.opened = false;
        this.updateSupport = null;
    }

    @Override
    public boolean closeResourceRequested(ResourceProvider resource) {

        try {
            getDataSource().delete();
        } catch (OGRUnsupportedFormatException e) {
            LOG.warn(String.format("Can not close resource requested %1s", resource), e);
        }
        this.envelope = null;
        this.newLayer = null;
        this.dataSource = null;
        this.opened = false;
        this.updateSupport = null;
        return true;
    }

    @Override
    public void resourceChanged(ResourceProvider resource) {

        try {
            getDataSource().delete();
        } catch (OGRUnsupportedFormatException e) {
            LOG.warn(String.format("Can not close resource requested %1s", resource), e);
        }
        this.envelope = null;
        this.newLayer = null;
        this.dataSource = null;
        this.opened = false;
        this.updateSupport = null;
    }
}
