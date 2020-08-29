INSERT INTO learning_process_status(id,name,description) values (0,'CEPTE Process Created',' Initial status for a peer evaluation process');
INSERT INTO learning_process_status(id,name,description) values (1,'CEPTE Process Available','The Learning process can be accessed by students');
INSERT INTO learning_process_status(id,name,description) values (2,'CEPTE Process Finished','The Learning process is finished');
INSERT INTO learning_process_status(id,name,description) values (3,'CEPTE Process results available','The Learning process is finished and results are published');

INSERT INTO rubric_type(id,type) values (1,'Assessment of Contents');
INSERT INTO rubric_type(id,type) values (2,'Assessment of Assessments');
INSERT INTO rubric_type(id,type) values (3,'Group based Assessment');
INSERT INTO rubric_type(id,type) values (4,'Group members Assessment');

--INSERT INTO cetpe_user(id,name,password) values (10,'Miguel','borrascas');
--INSERT INTO cetpe_user(id,name,password) values (11,'Hilda','Hilda');

INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent1', 'CRISTIAN', 'PERDIGUERO LOZANO');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent2', 'LAURA', 'PEREA LEÓN');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent3', 'JOSÉ MANUEL', 'PÉREZ ROMANA');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent4', 'DIANA', 'TEJERA BERENGUE');

INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent5', 'JUAN JOSÉ', 'FERNÁNDEZ DE VEGA');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent6', 'ISABEL', 'GIL RUIZ');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent7', 'ÁLVARO', 'LÓPEZ MARTÍNEZ');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent8', 'DANIEL', 'NAVARRO ROWLEY');

INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent9', 'ÁLVARO', 'JIMÉNEZ SOTO');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent10', 'GASPAR', 'MARTÍNEZ PIQUERAS');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent11', 'GONZALO', 'RESA ESPINOSA');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent12', 'ANA MARÍA', 'SAIZ GARCÍA');

INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent13', 'RAÚL', 'GARCÍA PÉREZ');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent14', 'SERGIO', 'RUIZ BARAJAS');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent15', 'CELIA', 'RUS DELGADO');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent16', 'MARTA', 'SÁEZ OLMEDILLA');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent17', 'IRENE', 'VILLA MURILLO');

INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent18', 'YUTING', 'CHEN');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent19', 'JORGE', 'MERINO PUERTA');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent20', 'JEAN FERNANDO', 'SELLÁN ARIAS');
