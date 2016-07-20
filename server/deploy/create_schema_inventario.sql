-- Deploy sixhiara:create_schema_inventario to pg

BEGIN;

CREATE SCHEMA inventario AUTHORIZATION sixhiara_owner;

CREATE TABLE "inventario"."fontes" (
gid SERIAL PRIMARY KEY,
"fonte" varchar(254),
"cod_fonte" varchar(254) UNIQUE,
"tip_fonte" varchar(254) REFERENCES inventario_dominios.tip_fonte(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"entidade" varchar(254),
"tecnico" varchar(254),
"estado_fon" varchar(254) REFERENCES inventario_dominios.estado(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"data" varchar(254),
"hora" varchar(254),
"provincia" varchar(254),
"distrito" varchar(254),
"posto_adm" varchar(254),
"nucleo" varchar(254),
"altitude" float8,
"distancia" float8,
"propiedad" varchar(254) REFERENCES inventario_dominios.propiedad(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"nome_prop" varchar(254),
"telefone" varchar(254),
"habitant" float8,
"superf" float8,
"n_reses" float8,
"tip_indus" varchar(254),
"coment_otr" varchar(254),
"tipo_pozo" varchar(254) REFERENCES inventario_dominios.tipo_pozo(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"prof_pozo" float8,
"diametro" float8,
"alt_brocal" float8,
"aaa" int2,
"tip_bomba" varchar(254),
"tip_motor" varchar(254),
"marca" varchar(254),
"alt_bomba" float8,
"caudal" float8,
"t_bombeo" float8,
"potencia" float8,
"estado" varchar(254) REFERENCES inventario_dominios.estado(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"rep_dista" float8,
"coment" varchar(254),
"red_monit" varchar(254)  REFERENCES inventario_dominios.red_monit(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"escritura" boolean,
"domestico" boolean,
"agricult" boolean,
"ganaderia" boolean,
"industria" boolean,
"outros" boolean,
"reperfor" boolean,
"limpezas" boolean,
"bombeo" boolean,
geom geometry('POINT', 32737)
);
CREATE INDEX ON "inventario"."fontes" USING GIST ("geom");

CREATE TABLE "inventario"."analise" (
gid SERIAL PRIMARY KEY,
"cod_fonte" varchar(50) REFERENCES inventario.fontes(cod_fonte)
	    ON UPDATE CASCADE
	    ON DELETE CASCADE,
"fonte" varchar(50),
"data_most" varchar(11),
"hora_most" varchar(6),
"c_tempera" varchar(9),
"c_conduct" varchar(9),
"c_cor" varchar(9),
"c_cheiro" varchar(50),
"c_ph" varchar(9),
"c_nitrat" varchar(9),
"c_nitrit" varchar(9),
"par_rango" varchar(9),
"cond_most" varchar(9) REFERENCES inventario_dominios.cond_most(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"com_most" varchar(9),
"laborator" varchar(9),
"data_anal" varchar(9),
"temperat" varchar(9),
"cor" varchar(50),
"turbidez" varchar(9),
"conductiv" varchar(9),
"ph" varchar(9),
"alcalin_f" varchar(9),
"alcalinid" varchar(9),
"carbonato" varchar(9),
"bicarbona" varchar(9),
"hidroxido" varchar(9),
"dureza" varchar(9),
"oxigeno_d" varchar(9),
"dbo" varchar(9),
"dqo" varchar(9),
"mo" varchar(9),
"sol_suspe" varchar(9),
"sol_disol" varchar(9),
"sol_total" varchar(9),
"nitratos" varchar(9),
"nitritos" varchar(9),
"coli_feca" varchar(9),
"coli_tot" varchar(9),
"e_coli" varchar(9),
"bac_het_t" varchar(9),
"cl_resid" varchar(9),
"cloruros" varchar(9),
"fosfatos" varchar(9),
"ca" varchar(9),
"mg" varchar(9),
"amonio" varchar(9),
"arsenico" varchar(9),
"k" varchar(9),
"na" varchar(9),
"si" varchar(9),
"fe" varchar(9),
"mn" varchar(9),
"al" varchar(9),
"b" varchar(9),
"cd" varchar(9),
"co" varchar(9),
"cr3" varchar(9),
"cr6" varchar(9),
"cu" varchar(9),
"hg" varchar(9),
"ni" varchar(9),
"pb" varchar(9),
"zn" varchar(9),
"comen_lab" varchar(50)
);

CREATE TABLE "inventario"."quantidade_agua" (
gid SERIAL PRIMARY KEY,
"cod_fonte" varchar(50) REFERENCES inventario.fontes(cod_fonte)
	    ON UPDATE CASCADE
	    ON DELETE CASCADE,
"data" varchar(11),
"hora" varchar(6),
"quan_agua" varchar(9),
"q_extraer" varchar(9)
);



CREATE TABLE "inventario"."acuiferos" (
gid SERIAL PRIMARY KEY,
"acuifero" varchar(50),
"cod_acuif" varchar(50) UNIQUE,
"tipo_hidr" varchar(254) REFERENCES inventario_dominios.tipo_hidr(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"tipo_lito" varchar(254) REFERENCES inventario_dominios.tipo_lito(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"tipo_poro" varchar(50) REFERENCES inventario_dominios.tipo_poro(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"geologia" varchar(254),
"profundid" varchar(50),
"cumprimen" varchar(50),
"coment" varchar(50),
geom geometry('MULTIPOLYGON', 32737)
);
CREATE INDEX ON "inventario"."acuiferos" USING GIST ("geom");



CREATE TABLE "inventario"."estacoes" (
gid SERIAL PRIMARY KEY,
"estazon" varchar(254),
"cod_estac" varchar(254) UNIQUE,
"tip_estac" varchar(254) REFERENCES inventario_dominios.tip_estac(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"provincia" varchar(254),
"distrito" varchar(254),
"posto_adm" varchar(254),
"nucleo" varchar(254),
"altitude" float8,
"bacia" varchar(254),
"cod_bacia" varchar(254),
"rio" varchar(254),
"estado" varchar(254) REFERENCES inventario_dominios.estado(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"ano_const" float8,
"gestao" varchar(254) REFERENCES inventario_dominios.gestao_estacoes(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"n_tecnico" float8,
"responsab" varchar(254),
"telefone" varchar(254),
"ano_inici" float8,
"ano_fin" float8,
"n_anos" float8,
"frec_toma" varchar(254) REFERENCES inventario_dominios.frecuencia(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"frec_regi" varchar(254) REFERENCES inventario_dominios.frecuencia(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"n_precis" varchar(254) REFERENCES inventario_dominios.precisao(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"n_lin_em" float8,
"n_t_outro" varchar(254),
"v_precis" varchar(254) REFERENCES inventario_dominios.precisao(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"n_molinet" float8,
"v_t_outro" varchar(254),
"tip_pluvi" varchar(254) REFERENCES inventario_dominios.tip_pluvi(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"n_pluviom" float8,
"coment" varchar(254),
"et_id" float8,
"nivel" boolean,
"lin_em" boolean,
"n_outros" boolean,
"velocidad" boolean,
"molinetes" boolean,
"v_outros" boolean,
"caudal" boolean,
"pluviomet" boolean,
geom geometry('POINT', 32737)
);
CREATE INDEX ON "inventario"."estacoes" USING GIST ("geom");



CREATE TABLE "inventario"."dados_hidrometricos" (
gid SERIAL PRIMARY KEY,
"cod_estac" varchar(50) REFERENCES inventario.estacoes(cod_estac)
	    ON UPDATE CASCADE
	    ON DELETE CASCADE,
"estazon" varchar(50),
"ano" varchar(6),
"n_med_ja" varchar(9),
"n_max_ja" varchar(9),
"n_min_ja" varchar(9),
"n_med_fe" varchar(9),
"n_max_fe" varchar(9),
"n_min_fe" varchar(9),
"n_med_ma" varchar(9),
"n_max_ma" varchar(9),
"n_min_ma" varchar(9),
"n_med_ab" varchar(9),
"n_max_ab" varchar(9),
"n_min_ab" varchar(9),
"n_med_mo" varchar(9),
"n_max_mo" varchar(9),
"n_min_mo" varchar(9),
"n_med_ju" varchar(9),
"n_max_ju" varchar(9),
"n_min_ju" varchar(9),
"n_med_jl" varchar(9),
"n_max_jl" varchar(9),
"n_min_jl" varchar(9),
"n_med_ag" varchar(9),
"n_max_ag" varchar(9),
"n_min_ag" varchar(9),
"n_med_se" varchar(9),
"n_max_se" varchar(9),
"n_min_se" varchar(9),
"n_med_ou" varchar(9),
"n_max_ou" varchar(9),
"n_min_ou" varchar(9),
"n_med_no" varchar(9),
"n_max_no" varchar(9),
"n_min_no" varchar(9),
"n_med_de" varchar(9),
"n_max_de" varchar(9),
"n_min_de" varchar(9),
"n_med_ano" varchar(9),
"n_max_ano" varchar(9),
"n_min_ano" varchar(9),
"q_med_ja" varchar(9),
"q_max_ja" varchar(9),
"q_min_ja" varchar(9),
"q_med_fe" varchar(9),
"q_max_fe" varchar(9),
"q_min_fe" varchar(9),
"q_med_ma" varchar(9),
"q_max_ma" varchar(9),
"q_min_ma" varchar(9),
"q_med_ab" varchar(9),
"q_max_ab" varchar(9),
"q_min_ab" varchar(9),
"q_med_mo" varchar(9),
"q_max_mo" varchar(9),
"q_min_mo" varchar(9),
"q_med_ju" varchar(9),
"q_max_ju" varchar(9),
"q_min_ju" varchar(9),
"q_med_jl" varchar(9),
"q_max_jl" varchar(9),
"q_min_jl" varchar(9),
"q_med_ag" varchar(9),
"q_max_ag" varchar(9),
"q_min_ag" varchar(9),
"q_med_se" varchar(9),
"q_max_se" varchar(9),
"q_min_se" varchar(9),
"q_med_ou" varchar(9),
"q_max_ou" varchar(9),
"q_min_ou" varchar(9),
"q_med_no" varchar(9),
"q_max_no" varchar(9),
"q_min_no" varchar(9),
"q_med_de" varchar(9),
"q_max_de" varchar(9),
"q_min_de" varchar(9),
"q_med_ano" varchar(9),
"q_max_ano" varchar(9),
"q_min_ano" varchar(9),
"e_med_ja" varchar(9),
"e_max_ja" varchar(9),
"e_min_ja" varchar(9),
"e_med_fe" varchar(9),
"e_max_fe" varchar(9),
"e_min_fe" varchar(9),
"e_med_ma" varchar(9),
"e_max_ma" varchar(9),
"e_min_ma" varchar(9),
"e_med_ab" varchar(9),
"e_max_ab" varchar(9),
"e_min_ab" varchar(9),
"e_med_mo" varchar(9),
"e_max_mo" varchar(9),
"e_min_mo" varchar(9),
"e_med_ju" varchar(9),
"e_max_ju" varchar(9),
"e_min_ju" varchar(9),
"e_med_jl" varchar(9),
"e_max_jl" varchar(9),
"e_min_jl" varchar(9),
"e_med_ag" varchar(9),
"e_max_ag" varchar(9),
"e_min_ag" varchar(9),
"e_med_se" varchar(9),
"e_max_se" varchar(9),
"e_min_se" varchar(9),
"e_med_ou" varchar(9),
"e_max_ou" varchar(9),
"e_min_ou" varchar(9),
"e_med_no" varchar(9),
"e_max_no" varchar(9),
"e_min_no" varchar(9),
"e_med_de" varchar(9),
"e_max_de" varchar(9),
"e_min_de" varchar(9),
"e_med_ano" varchar(9),
"e_max_ano" varchar(9),
"e_min_ano" varchar(9));



CREATE TABLE "inventario"."dados_pluviometricos" (
gid SERIAL PRIMARY KEY,
"cod_estac" varchar(50) REFERENCES inventario.estacoes(cod_estac)
	    ON UPDATE CASCADE
	    ON DELETE CASCADE,
"estazon" varchar(50),
"ano" varchar(6),
"to_mes_ja" varchar(9),
"d_chuv_ja" varchar(9),
"media_ja" varchar(9),
"max_d_ja" varchar(9),
"to_mes_fe" varchar(9),
"d_chuv_fe" varchar(9),
"media_fe" varchar(9),
"max_d_fe" varchar(9),
"to_mes_ma" varchar(9),
"d_chuv_ma" varchar(9),
"media_ma" varchar(9),
"max_d_ma" varchar(9),
"to_mes_ab" varchar(9),
"d_chuv_ab" varchar(9),
"media_ab" varchar(9),
"max_d_ab" varchar(9),
"to_mes_mo" varchar(9),
"d_chuv_mo" varchar(9),
"media_mo" varchar(9),
"max_d_mo" varchar(9),
"to_mes_ju" varchar(9),
"d_chuv_ju" varchar(9),
"media_ju" varchar(9),
"max_d_ju" varchar(9),
"to_mes_jl" varchar(9),
"d_chuv_jl" varchar(9),
"media_jl" varchar(9),
"max_d_jl" varchar(9),
"to_mes_ag" varchar(9),
"d_chuv_ag" varchar(9),
"media_ag" varchar(9),
"max_d_ag" varchar(9),
"to_mes_se" varchar(9),
"d_chuv_se" varchar(9),
"media_se" varchar(9),
"max_d_se" varchar(9),
"to_mes_ou" varchar(9),
"d_chuv_ou" varchar(9),
"media_ou" varchar(9),
"max_d_ou" varchar(9),
"to_mes_no" varchar(9),
"d_chuv_no" varchar(9),
"media_no" varchar(9),
"max_d_no" varchar(9),
"to_mes_de" varchar(9),
"d_chuv_de" varchar(9),
"media_de" varchar(9),
"max_d_de" varchar(9),
"c_tot_ano" varchar(9),
"d_chu_tot" varchar(9),
"c_med_ano" varchar(9),
"c_max_ano" varchar(9),
"d_chu_con" varchar(9),
"d_no_chu" varchar(9));

CREATE TABLE "inventario"."barragens" (
gid SERIAL PRIMARY KEY,
"barragem" varchar(254),
"cod_barra" varchar(254) UNIQUE,
"tip_barra" varchar(254) REFERENCES inventario_dominios.tip_barra(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"provincia" varchar(254),
"distrito" varchar(254),
"posto_adm" varchar(254),
"nucleo" varchar(254),
"altitude" float8,
"bacia" varchar(254),
"cod_bacia" varchar(254),
"rio" varchar(254),
"estado" varchar(254) REFERENCES inventario_dominios.estado(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"ano_const" float8,
"gestao" varchar(254) REFERENCES inventario_dominios.gestao(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"responsab" varchar(254),
"telefone" varchar(254),
"tip_bar_2" varchar(254) REFERENCES inventario_dominios.tip_bar_2(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"vol_total" float8,
"vol_act" varchar(254),
"h_altitud" float8,
"estrutura" varchar(254),
"n_inspec" float8,
"fonte_loc" varchar(254),
"fonte_inf" varchar(254),
"fiab_info" varchar(254) REFERENCES inventario_dominios.fiab_info(key)
	    ON UPDATE CASCADE
	    ON DELETE NO ACTION,
"coment" varchar(254),
"inspecoes" boolean,
"u_hidroel" boolean,
"u_irriga" boolean,
"u_abast" boolean,
geom geometry('POINT', 32737)
);

CREATE INDEX ON "inventario"."barragens" USING GIST ("geom");




GRANT USAGE ON SCHEMA inventario TO inventario_read;
GRANT SELECT ON ALL TABLES IN SCHEMA inventario TO inventario_read;

GRANT INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA inventario TO inventario_write;




COMMIT;
