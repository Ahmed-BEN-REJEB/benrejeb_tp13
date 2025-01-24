-- Mettre ici les données qu'on veut charger avant un test spécifique
-- en utilisant l'annotation @SQL("nom_du_fichier.sql")
-- Le fichier ne peut pas être vide
INSERT INTO Client(code, societe, contact, fonction, adresse, ville, region, code_postal, pays, telephone, fax) VALUES
    ( 'XCOM', 'Ce client n''a pas de commande', 'Maria Anders', 'Représentant(e)', 'Obere Str. 57', 'Berlin', NULL, '12209', 'Allemagne', '030-0074321', '030-0076545');
SELECT 1 AS DUMMY;
