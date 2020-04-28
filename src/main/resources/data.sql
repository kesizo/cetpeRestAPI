INSERT INTO learning_process_status(id,name,description) values (0,'CEPTE Process Created',' Initial status for a peer evaluation process');
INSERT INTO learning_process_status(id,name,description) values (1,'CEPTE Process Available','The Learning process can be accessed by students');
INSERT INTO learning_process_status(id,name,description) values (2,'CEPTE Process Finished','The Learning process is finished');
INSERT INTO learning_process_status(id,name,description) values (3,'CEPTE Process results available','The Learning process is finished and results are published');

INSERT INTO rubric_type(id,type) values (1,'Assessment of Contents');
INSERT INTO rubric_type(id,type) values (2,'Assessment of Assesments');
INSERT INTO rubric_type(id,type) values (3,'Group based Assessment');
INSERT INTO rubric_type(id,type) values (4,'Group members Assessment');

INSERT INTO cetpe_user(id,name,password) values (10,'Miguel','borrascas');
INSERT INTO cetpe_user(id,name,password) values (11,'Hilda','Hilda');

INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent1', 'studentName1', 'studentLastName1');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent2', 'studentName2', 'studentLastName2');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent3', 'studentName3', 'studentLastName3');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent4', 'studentName4', 'studentLastName4');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent5', 'studentName5', 'studentLastName5');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent6', 'studentName6', 'studentLastName6');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent7', 'studentName7', 'studentLastName7');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent8', 'studentName8', 'studentLastName8');
INSERT INTO public.learning_student(username, first_name, last_name) VALUES ('usernameStudent9', 'studentName9', 'studentLastName9');
