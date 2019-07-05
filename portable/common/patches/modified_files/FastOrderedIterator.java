/**
 * gvSIG. Desktop Geographic Information System.
 *
 * Copyright (C) 2007-2013 gvSIG Association.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
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
package org.gvsig.fmap.dal.feature.impl.featureset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.gvsig.fmap.dal.exception.DataException;
import org.gvsig.tools.dispose.DisposableIterator;
import org.gvsig.fmap.dal.feature.Feature;
import org.gvsig.fmap.dal.feature.impl.DefaultFeature;
import org.gvsig.fmap.dal.feature.spi.FeatureProvider;
import org.gvsig.tools.exception.BaseException;

public class FastOrderedIterator extends DefaultIterator {
	DefaultFeature myFeature;
	protected Feature lastFeature = null;

	FastOrderedIterator(DefaultFeatureSet featureSet, Iterator iterator, long index) {
		super(featureSet);
		try {
			this.initializeFeature();
			if (featureSet.orderedData == null) {
				// FIXME QUE PASA CON SIZE > Integer.MAX_VALUE ?????
	
				List data = new ArrayList();
				Object item;
				while (iterator.hasNext()) {
					item = iterator.next();
					if (item instanceof DefaultFeature){
						data.add(((DefaultFeature) item).getData().getCopy());
					} else {
						data.add(((FeatureProvider)item).getCopy());
					}
				}
				Collections.sort(data, new FeatureProviderComparator(featureSet.store,
						featureSet.query.getOrder()));
				featureSet.orderedData = data;
			}
	
			if (index < Integer.MAX_VALUE) {
				this.iterator = featureSet.orderedData.listIterator((int) index);
			} else {
				this.iterator = featureSet.orderedData.iterator();
				this.skypto(index);
			}
		}
		finally {			
			if (iterator instanceof DisposableIterator) {
				((DisposableIterator) iterator).dispose();
			}
		}
	}

	public FastOrderedIterator(DefaultFeatureSet featureSet, long index) {
		super(featureSet);
		this.initializeFeature();

		if (index < Integer.MAX_VALUE) {
			this.iterator = featureSet.orderedData.listIterator((int) index);
		} else {
			this.iterator = featureSet.orderedData.iterator();
			this.skypto(index);
		}
	}

	protected DefaultFeature createFeature(FeatureProvider fData) {
		this.myFeature.setData(fData);
		return this.myFeature;
	}

	protected void initializeFeature() {
		myFeature = new DefaultFeature(fset.store);
	}

	public Object next() {
		lastFeature = null;
		lastFeature = (Feature) super.next();
		return lastFeature;
	}

	public void remove() {
		if (!fset.store.isEditing()) {
			throw new UnsupportedOperationException();
		}
		if (this.lastFeature == null) {
			throw new IllegalStateException();
		}
		try {
			fset.store.delete(this.lastFeature);
		} catch (DataException e) {
			// FIXME Cambiar excepcion a una Runtime de DAL
			throw new RuntimeException(e);
		}
		this.iterator.remove();
		this.initializeFeature();
	}

	protected void doDispose() throws BaseException {
		super.doDispose();
		myFeature = null;
		lastFeature = null;
	}
}

