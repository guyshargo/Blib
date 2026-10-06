-- MySQL dump 10.13  Distrib 8.0.46, for Win64 (x86_64)
--
-- Host: localhost    Database: blib
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `activities`
--

DROP TABLE IF EXISTS `activities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activities` (
  `member_id` int NOT NULL,
  `activity_name` varchar(255) NOT NULL,
  `entity_id` int DEFAULT NULL,
  `activity_date` datetime NOT NULL,
  PRIMARY KEY (`activity_name`,`member_id`,`activity_date`),
  KEY `fk_activities_member_id_idx` (`member_id`),
  CONSTRAINT `fk_activities_member_id` FOREIGN KEY (`member_id`) REFERENCES `members` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activities`
--

LOCK TABLES `activities` WRITE;
/*!40000 ALTER TABLE `activities` DISABLE KEYS */;
INSERT INTO `activities` VALUES (101,'borrow',1,'2026-09-01 18:22:27'),(101,'borrow',7,'2026-09-01 18:22:34'),(103,'borrow',4,'2026-09-11 18:22:54'),(104,'borrow',7,'2026-09-05 18:22:38'),(104,'borrow',10,'2026-09-15 21:57:31'),(104,'borrow',6,'2026-09-26 20:21:43'),(104,'borrow',10,'2026-09-27 00:10:41'),(105,'borrow',3,'2026-09-11 18:22:46'),(108,'borrow',1,'2026-09-21 18:23:09'),(109,'borrow',6,'2026-09-20 18:23:01'),(109,'borrow',9,'2026-09-28 01:25:21'),(102,'cancelOrder',10,'2026-09-27 00:13:05'),(105,'cancelOrder',10,'2026-09-27 00:15:13'),(101,'freezeStatus',NULL,'2026-09-23 19:25:10'),(107,'freezeStatus',NULL,'2026-09-26 20:04:08'),(101,'lateBookReturn',7,'2026-09-24 18:28:06'),(103,'lateBookReturn',4,'2026-09-26 18:28:14'),(105,'lateBookReturn',3,'2026-09-26 18:28:36'),(102,'order',10,'2026-09-27 00:12:02'),(103,'order',10,'2026-09-27 00:13:23'),(104,'order',10,'2026-09-27 00:15:30'),(105,'order',10,'2026-09-27 00:11:29'),(105,'order',6,'2026-09-28 01:17:23'),(110,'registerMember',NULL,'2026-09-07 20:03:05'),(101,'returning',1,'2026-09-15 18:28:03'),(104,'returning',7,'2026-09-19 18:28:27'),(109,'returning',6,'2026-09-28 01:22:40');
/*!40000 ALTER TABLE `activities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_copies`
--

DROP TABLE IF EXISTS `book_copies`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_copies` (
  `copy_id` int NOT NULL,
  `book_id` int DEFAULT NULL,
  `borrow_status` enum('Borrowed','NotBorrowed') DEFAULT NULL,
  `shelf_location` varchar(255) DEFAULT NULL,
  `barcode` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`copy_id`),
  KEY `book_id_idx` (`book_id`),
  CONSTRAINT `fk_book_copies_book_id` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_copies`
--

LOCK TABLES `book_copies` WRITE;
/*!40000 ALTER TABLE `book_copies` DISABLE KEYS */;
INSERT INTO `book_copies` VALUES (1,1,'NotBorrowed','A1','D001'),(2,1,'Borrowed','A2','D002'),(3,2,'NotBorrowed','B1','D003'),(4,2,'NotBorrowed','B2','D004'),(5,3,'NotBorrowed','C1','D005'),(6,3,'NotBorrowed','C2','D006'),(7,4,'NotBorrowed','D1','D007'),(8,4,'NotBorrowed','D2','D008'),(9,5,'NotBorrowed','E1','D009'),(10,5,'NotBorrowed','E2','D010'),(11,6,'NotBorrowed','F1','D011'),(12,6,'Borrowed','F2','D012'),(13,7,'NotBorrowed','G1','D013'),(14,7,'NotBorrowed','G2','D014'),(15,8,'NotBorrowed','H1','D015'),(16,8,'NotBorrowed','H2','D016'),(17,9,'Borrowed','I1','D017'),(18,9,'NotBorrowed','I2','D018'),(19,10,'Borrowed','J1','D019'),(20,10,'Borrowed','J2','D020'),(21,1,'NotBorrowed','A3','D021'),(22,2,'NotBorrowed','B3','D022'),(23,3,'NotBorrowed','C3','D023'),(24,4,'NotBorrowed','D3','D024'),(25,5,'NotBorrowed','E3','D025'),(26,11,'NotBorrowed','K1','D026'),(27,11,'NotBorrowed','K2','D027'),(28,11,'NotBorrowed','K3','D028'),(29,12,'NotBorrowed','L1','D029'),(30,12,'NotBorrowed','L2','D030'),(31,12,'NotBorrowed','L3','D031'),(32,13,'NotBorrowed','M1','D032'),(33,13,'NotBorrowed','M2','D033'),(34,14,'NotBorrowed','N1','D034'),(35,14,'NotBorrowed','N2','D035'),(36,14,'NotBorrowed','N3','D036'),(37,15,'NotBorrowed','O1','D037'),(38,15,'NotBorrowed','O2','D038'),(39,16,'NotBorrowed','P1','D039'),(40,16,'NotBorrowed','P2','D040'),(41,17,'NotBorrowed','Q1','D041'),(42,17,'NotBorrowed','Q2','D042'),(43,18,'NotBorrowed','R1','D043'),(44,18,'NotBorrowed','R2','D044'),(45,18,'NotBorrowed','R3','D045'),(46,19,'NotBorrowed','S1','D046'),(47,19,'NotBorrowed','S2','D047'),(48,20,'NotBorrowed','T1','D048'),(49,20,'NotBorrowed','T2','D049'),(50,21,'NotBorrowed','U1','D050'),(51,21,'NotBorrowed','U2','D051'),(52,22,'NotBorrowed','V1','D052'),(53,22,'NotBorrowed','V2','D053'),(54,23,'NotBorrowed','W1','D054'),(55,23,'NotBorrowed','W2','D055'),(56,24,'NotBorrowed','W3','D056'),(57,24,'NotBorrowed','W4','D057'),(58,25,'NotBorrowed','X1','D058'),(59,25,'NotBorrowed','X2','D059'),(60,26,'NotBorrowed','X3','D060'),(61,26,'NotBorrowed','X4','D061'),(62,27,'NotBorrowed','Y1','D062'),(63,27,'NotBorrowed','Y2','D063'),(64,28,'NotBorrowed','Y3','D064'),(65,28,'NotBorrowed','Y4','D065'),(66,29,'NotBorrowed','Z1','D066'),(67,29,'NotBorrowed','Z2','D067'),(68,30,'NotBorrowed','Z3','D068'),(69,30,'NotBorrowed','Z4','D069'),(70,31,'NotBorrowed','AA1','D070'),(71,31,'NotBorrowed','AA2','D071'),(72,32,'NotBorrowed','AA3','D072'),(73,32,'NotBorrowed','AA4','D073'),(74,33,'NotBorrowed','BB1','D074'),(75,33,'NotBorrowed','BB2','D075'),(76,34,'NotBorrowed','BB3','D076'),(77,34,'NotBorrowed','BB4','D077'),(78,35,'NotBorrowed','CC1','D078'),(79,35,'NotBorrowed','CC2','D079'),(80,36,'NotBorrowed','CC3','D080'),(81,36,'NotBorrowed','CC4','D081'),(82,37,'NotBorrowed','DD1','D082'),(83,37,'NotBorrowed','DD2','D083'),(84,38,'NotBorrowed','DD3','D084'),(85,38,'NotBorrowed','DD4','D085'),(86,39,'NotBorrowed','EE1','D086'),(87,39,'NotBorrowed','EE2','D087'),(88,40,'NotBorrowed','EE3','D088'),(89,40,'NotBorrowed','EE4','D089'),(90,41,'NotBorrowed','FF1','D090'),(91,41,'NotBorrowed','FF2','D091'),(92,42,'NotBorrowed','FF3','D092'),(93,42,'NotBorrowed','FF4','D093'),(94,43,'NotBorrowed','GG1','D094'),(95,43,'NotBorrowed','GG2','D095'),(96,44,'NotBorrowed','GG3','D096'),(97,44,'NotBorrowed','GG4','D097'),(98,45,'NotBorrowed','HH1','D098'),(99,45,'NotBorrowed','HH2','D099'),(100,46,'NotBorrowed','HH3','D100'),(101,46,'NotBorrowed','HH4','D101'),(102,47,'NotBorrowed','II1','D102'),(103,47,'NotBorrowed','II2','D103'),(104,48,'NotBorrowed','II3','D104'),(105,48,'NotBorrowed','II4','D105'),(106,49,'NotBorrowed','JJ1','D106'),(107,49,'NotBorrowed','JJ2','D107'),(108,50,'NotBorrowed','JJ3','D108'),(109,50,'NotBorrowed','JJ4','D109'),(110,51,'NotBorrowed','KK1','D110'),(111,51,'NotBorrowed','KK2','D111'),(112,52,'NotBorrowed','KK3','D112'),(113,52,'NotBorrowed','KK4','D113'),(114,53,'NotBorrowed','LL1','D114'),(115,53,'NotBorrowed','LL2','D115'),(116,54,'NotBorrowed','MM1','D116'),(117,54,'NotBorrowed','MM2','D117'),(118,55,'NotBorrowed','NN1','D118'),(119,55,'NotBorrowed','NN2','D119'),(120,56,'NotBorrowed','OO1','D120'),(121,56,'NotBorrowed','OO2','D121'),(122,57,'NotBorrowed','PP1','D122'),(123,57,'NotBorrowed','PP2','D123'),(124,58,'NotBorrowed','QQ1','D124'),(125,58,'NotBorrowed','QQ2','D125'),(126,59,'NotBorrowed','RR1','D126'),(127,59,'NotBorrowed','RR2','D127'),(128,60,'NotBorrowed','SS1','D128'),(129,60,'NotBorrowed','SS2','D129'),(130,61,'NotBorrowed','TT1','D130'),(131,61,'NotBorrowed','TT2','D131'),(132,62,'NotBorrowed','UU1','D132'),(133,62,'NotBorrowed','UU2','D133'),(134,63,'NotBorrowed','VV1','D134'),(135,63,'NotBorrowed','VV2','D135'),(136,64,'NotBorrowed','WW1','D136'),(137,64,'NotBorrowed','WW2','D137'),(138,65,'NotBorrowed','XX1','D138'),(139,65,'NotBorrowed','XX2','D139'),(140,66,'NotBorrowed','YY1','D140'),(141,66,'NotBorrowed','YY2','D141'),(142,67,'NotBorrowed','ZZ1','D142'),(143,67,'NotBorrowed','ZZ2','D143'),(144,68,'NotBorrowed','AB1','D144'),(145,68,'NotBorrowed','AB2','D145'),(146,69,'NotBorrowed','AC1','D146'),(147,69,'NotBorrowed','AC2','D147'),(148,70,'NotBorrowed','AD1','D148'),(149,70,'NotBorrowed','AD2','D149'),(150,71,'NotBorrowed','AE1','D150'),(151,71,'NotBorrowed','AE2','D151'),(152,72,'NotBorrowed','AF1','D152'),(153,72,'NotBorrowed','AF2','D153'),(154,73,'NotBorrowed','AG1','D154'),(155,73,'NotBorrowed','AG2','D155'),(156,74,'NotBorrowed','AH1','D156'),(157,74,'NotBorrowed','AH2','D157'),(158,75,'NotBorrowed','AI1','D158'),(159,75,'NotBorrowed','AI2','D159'),(160,76,'NotBorrowed','AJ1','D160'),(161,76,'NotBorrowed','AJ2','D161');
/*!40000 ALTER TABLE `book_copies` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `book_id` int NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `genre` varchar(255) DEFAULT NULL,
  `number_of_copies` int DEFAULT NULL,
  `number_of_borrowed_copies` int DEFAULT NULL,
  `is_ordered` enum('yes','no') DEFAULT NULL,
  `number_of_orders` int DEFAULT NULL,
  `keywords` text,
  `summary` text,
  PRIMARY KEY (`book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES (1,'To Kill a Mockingbird','Fiction',3,1,'no',0,'classic, racism, justice','A powerful story about racial injustice, morality, and human compassion, set in the Deep South during the 1930s.'),(2,'A Brief History of Time','Science',3,0,'no',0,'cosmology, universe, physics','An exploration of cosmology and the universe\'s mysteries, explaining complex scientific concepts for a general audience'),(3,'Sapiens A Brief History of Humankind','History',3,0,'no',0,'evolution, anthropology, civilization','A fascinating journey through human history, exploring how Homo sapiens evolved and shaped the world.'),(4,'Clean Code','Technology',3,0,'no',0,'programming, software, development','A guide to writing clean, efficient, and maintainable software, emphasizing the principles of professional programming.'),(5,'Mathematics Its Content Methods and Meaning','Mathematics',3,0,'no',0,'theory, algebra, geometry','A comprehensive overview of mathematics\' core ideas and its influence on science, logic, and the modern world.'),(6,'Pride and Prejudice','Literature',2,1,'yes',1,'romance, society, classic','A romantic tale of manners and misunderstandings, highlighting the complex dynamics of love, class, and family in Regency-era England.'),(7,'The Story of Art','Art',2,0,'no',0,'history, painting, sculpture','An engaging account of the evolution of art through the centuries, connecting historical contexts with artistic movements.'),(8,'The Republic','Philosophy',2,0,'no',0,'ethics, politics, justice','A philosophical dialogue by Plato examining justice, politics, and the ideal society through timeless debates and arguments.'),(9,'The Body A Guide for Occupants','Health',2,1,'no',0,'biology, anatomy, fitness','A witty and informative tour of the human body, revealing its intricate workings and fascinating features.'),(10,'1984','Fiction',2,2,'yes',2,'dystopia, surveillance, totalitarianism','A chilling dystopian tale of totalitarianism, surveillance, and the loss of individuality in a repressive state.'),(11,'Dune','Sci-Fi',3,0,'no',0,'desert, space, arrakis, classic','A sprawling sci-fi epic set on the desert planet Arrakis, revolving around the control of the universe\'s most valuable substance.'),(12,'Atomic Habits','Self-Help',3,0,'no',0,'habits, productivity, growth, psychology','A practical framework for improving every day by building good habits and breaking bad ones.'),(13,'The Pragmatic Programmer','Technology',2,0,'no',0,'coding, software, professional, career','An essential guide to software engineering mastery, covering everything from personal responsibility to architectural decay.'),(14,'Project Hail Mary','Sci-Fi',3,0,'no',0,'space, survival, aliens, science','A lone astronaut must save Earth from disaster using solely his scientific knowledge and an unexpected ally.'),(15,'Shoe Dog','Biography',2,0,'no',0,'business, nike, memoir, entrepreneurship','A candid and riveting memoir by the creator of Nike, detailing the company\'s early struggles and ultimate triumph.'),(16,'The Silent Patient','Thriller',2,0,'no',0,'psychological, mystery, murder, suspense','A shocking psychological thriller about a woman\'s act of violence against her husband and the therapist obsessed with uncovering her motive.'),(17,'Tomorrow, and Tomorrow, and Tomorrow','Fiction',2,0,'no',0,'gaming, relationships, drama, love','A sweeping narrative following two childhood friends who reunite to create a wildly successful video game, changing their lives forever.'),(18,'Fourth Wing','Fantasy',3,0,'no',0,'dragons, magic, romance, war','A high-stakes fantasy adventure set in an elite and brutal war college for dragon riders.'),(19,'The Psychology of Money','Finance',2,0,'no',0,'wealth, mindset, economics, investing','Timeless lessons on wealth, greed, and happiness, explaining how people make financial decisions.'),(20,'Dark Matter','Thriller',2,0,'no',0,'multiverse, suspense, science, physics','A mind-bending thriller about a man who is abducted, only to wake up in a world where his life is entirely different.'),(21,'The Midnight Library','Fiction',2,0,'no',0,'choices, life, philosophy, regrets','Between life and death there is a library containing infinite books, each telling the story of another reality you could have lived.'),(22,'System Design Interview','Technology',2,0,'no',0,'interviews, architecture, scaling, backend','An insider\'s guide to tackling complex system design interviews with step-by-step frameworks and real-world examples.'),(23,'The Great Gatsby','Literature',2,0,'no',0,'jazz, wealth, society, romance','A tragic story of Jay Gatsby and his pursuit of the American Dream in the 1920s.'),(24,'Moby Dick','Literature',2,0,'no',0,'whales, ocean, obsession, adventure','Captain Ahab sets out on a relentless and doomed quest to hunt the legendary white whale.'),(25,'War and Peace','Literature',2,0,'no',0,'russia, history, war, society','A sweeping epic chronicling the French invasion of Russia and its impact on five aristocratic families.'),(26,'Crime and Punishment','Literature',2,0,'no',0,'guilt, psychology, murder, morality','A gripping psychological drama about a young man who commits a murder to test his own theories of morality.'),(27,'Jane Eyre','Literature',2,0,'no',0,'romance, gothic, independence','An orphan overcomes her abusive childhood to become a governess and falls in love with her mysterious employer.'),(28,'The Catcher in the Rye','Fiction',2,0,'no',0,'youth, rebellion, angst, new york','A teenager navigates the complexities of adolescence and alienation over a few days in New York City.'),(29,'Animal Farm','Sci-Fi',2,0,'no',0,'dystopia, rebellion, politics, satire','A satirical allegory of the Russian Revolution told through a group of farm animals who overthrow their human farmer.'),(30,'Brave New World','Sci-Fi',2,0,'no',0,'dystopia, future, technology, control','A dystopian vision of a futuristic society genetically engineered for strict social hierarchy and superficial happiness.'),(31,'The Hobbit','Fantasy',2,0,'no',0,'dragons, adventure, magic, middle-earth','A comfortable hobbit is swept into an epic quest to reclaim a lost dwarf kingdom from a fearsome dragon.'),(32,'Harry Potter and the Sorcerer\'s Stone','Fantasy',2,0,'no',0,'magic, school, wizards, fantasy','An orphaned boy discovers he is a wizard and begins his education at Hogwarts School of Witchcraft and Wizardry.'),(33,'The Fellowship of the Ring','Fantasy',2,0,'no',0,'elves, rings, quest, darkness','The first volume of the epic quest to destroy the One Ring and defeat the dark lord Sauron.'),(34,'Foundation','Sci-Fi',2,0,'no',0,'empire, space, future, psychology','A mathematician develops a method to predict the fall of the galactic empire and works to preserve human knowledge.'),(35,'Neuromancer','Sci-Fi',2,0,'no',0,'cyberpunk, hacking, ai, future','A washed-up computer hacker is hired for one last job that brings him into conflict with a powerful artificial intelligence.'),(36,'The Martian','Sci-Fi',2,0,'no',0,'mars, survival, science, space','An astronaut stranded on Mars must use his ingenuity and botanical skills to survive until a rescue mission can reach him.'),(37,'The Girl with the Dragon Tattoo','Thriller',2,0,'no',0,'mystery, murder, hacker, sweden','A journalist and a brilliant but troubled hacker team up to solve a decades-old disappearance.'),(38,'Gone Girl','Thriller',2,0,'no',0,'marriage, disappearance, twist, psychological','A man becomes the prime suspect when his wife goes missing on their fifth wedding anniversary.'),(39,'The Da Vinci Code','Mystery',2,0,'no',0,'religion, history, art, cryptography','A symbologist uncovers a religious mystery hidden in the works of Leonardo da Vinci that could rock the foundations of Christianity.'),(40,'And Then There Were None','Mystery',2,0,'no',0,'island, murder, classic, suspense','Ten strangers are invited to an isolated island, where they are picked off one by one according to a nursery rhyme.'),(41,'Steve Jobs','Biography',2,0,'no',0,'apple, technology, business, life','The exclusive, authorized biography of the visionary and demanding creator of Apple.'),(42,'The Diary of a Young Girl','Biography',2,0,'no',0,'history, wwii, holocaust, poignant','The true, harrowing, and deeply human diary kept by Anne Frank while hiding from the Nazis in Amsterdam.'),(43,'Thinking, Fast and Slow','Psychology',2,0,'no',0,'mind, decisions, economics, behavior','A groundbreaking tour of the mind, explaining the two systems that drive the way we think and make choices.'),(44,'The Power of Habit','Self-Help',2,0,'no',0,'habits, change, routine, science','An exploration of the science behind why habits exist and how they can be changed to improve our lives.'),(45,'Outliers','Sociology',2,0,'no',0,'success, culture, timing, psychology','An investigation into what makes high achievers different, focusing on cultural background, timing, and hidden advantages.'),(46,'In Cold Blood','History',2,0,'no',0,'true crime, murder, journalism, kansas','The pioneering true crime novel detailing the brutal 1959 murder of a Kansas farming family.'),(47,'Into Thin Air','History',2,0,'no',0,'everest, survival, disaster, climbing','A personal account of the 1996 Mount Everest disaster that claimed the lives of several climbers.'),(48,'The Alchemist','Fiction',2,0,'no',0,'journey, destiny, dreams, philosophy','A mystical story about an Andalusian shepherd boy who travels in search of a worldly treasure.'),(49,'The Kite Runner','Fiction',2,0,'no',0,'afghanistan, redemption, friendship, war','A story of friendship and betrayal set against the backdrop of a changing and war-torn Afghanistan.'),(50,'The Book Thief','Fiction',2,0,'no',0,'wwii, death, books, germany','Narrated by Death, this is the story of a young girl in Nazi Germany who steals books and shares them with her neighbors.'),(51,'The Hunger Games','Sci-Fi',2,0,'no',0,'dystopia, survival, games, rebellion','In a ruined future North America, teenagers are forced to fight to the death in an annual televised event.'),(52,'Catch-22','Literature',2,0,'no',0,'satire, war, military, absurd','A satirical and absurd look at the bureaucracy of war, following a bombardier attempting to maintain his sanity during WWII.'),(53,'Harry Potter and the Chamber of Secrets','Fantasy',2,0,'no',0,'magic, wizards, hogwarts, chamber, mystery','Harry returns for his second year at Hogwarts, where mysterious petrifications terrorize students.'),(54,'Harry Potter and the Prisoner of Azkaban','Fantasy',2,0,'no',0,'magic, dementors, azkaban, sirius black','An infamous prisoner escapes from Azkaban fortress, seemingly targeting Harry Potter.'),(55,'The Two Towers','Fantasy',2,0,'no',0,'middle-earth, ring, frodo, fellowship, rohan','The Fellowship is broken as Frodo and Sam journey to Mordor while others defend Rohan.'),(56,'The Return of the King','Fantasy',2,0,'no',0,'middle-earth, gondor, battle, ring, finale','The final battle for Middle-earth begins as Frodo approaches the fires of Mount Doom.'),(57,'A Game of Thrones','Fantasy',2,0,'no',0,'westeros, dragons, politics, stark, lannister','Noble families vie for control of the Iron Throne amidst political intrigue and rising supernatural threats.'),(58,'Catching Fire','Sci-Fi',2,0,'no',0,'dystopia, rebellion, survival, panem','Katniss and Peeta become targets of the Capitol after their rebellion sparks hope across Panem.'),(59,'The Lightning Thief','Fantasy',2,0,'no',0,'mythology, gods, percy jackson, quest, monsters','A modern teenager discovers he is a demigod son of Poseidon and is accused of stealing Zeus\'s lightning bolt.'),(60,'The Last Wish','Fantasy',2,0,'no',0,'witcher, monsters, geralt, magic, fantasy','Geralt of Rivia is a mutant witcher who hunts deadly monsters in a dark fantasy realm.'),(61,'The Lion, the Witch and the Wardrobe','Fantasy',2,0,'no',0,'narnia, wardrobe, aslan, magic, winter','Four siblings step through an enchanted wardrobe into the winter land of Narnia.'),(62,'A Court of Thorns and Roses','Fantasy',2,0,'no',0,'fae, magic, curse, high fantasy, romance','A mortal huntress is dragged into the treacherous faerie lands after slaying a wolf.'),(63,'It Ends with Us','Romance',2,0,'no',0,'contemporary, relationships, drama, love, emotional','A heartbreaking love story exploring resilience, difficult personal choices, and breaking toxic cycles.'),(64,'The Seven Husbands of Evelyn Hugo','Romance',2,0,'no',0,'hollywood, fame, secrets, glamorous, biography','An aging Hollywood cinema icon finally reveals the truth about her glamorous life and seven marriages.'),(65,'Normal People','Romance',2,0,'no',0,'coming of age, modern romance, connection, youth','An intimate exploration of how two young people subtly change each other\'s lives over years.'),(66,'Outlander','Romance',2,0,'no',0,'time travel, scotland, historical romance, highlands','A WWII nurse is transported back to 18th-century Scotland and finds herself torn between two centuries.'),(67,'Me Before You','Romance',2,0,'no',0,'drama, romance, emotional, tragedy, tearjerker','An unlikely bond forms between an eccentric caregiver and a paralyzed former adventurer.'),(68,'The Notebook','Romance',2,0,'no',0,'classic romance, southern, devotion, timeless','A timeless love story spanning decades, recounting the enduring passion between Noah and Allie.'),(69,'Wuthering Heights','Literature',2,0,'no',0,'gothic, passion, obsession, yorkshire, classic','The tempestuous and destructive love between Heathcliff and Catherine on the Yorkshire moors.'),(70,'Emma','Literature',2,0,'no',0,'regency, matchmaking, wit, high society, austen','A clever, self-satisfied young woman meddles in the romantic lives of her neighbors with unintended results.'),(71,'The Shining','Horror',2,0,'no',0,'hotel, winter, psychological horror, isolation','A family serves as winter caretakers of an isolated hotel with a dark and supernatural past.'),(72,'Dracula','Horror',2,0,'no',0,'vampire, gothic horror, victorian, epistolary','The legendary Transylvanian nobleman travels to Victorian England in search of new blood.'),(73,'A Study in Scarlet','Mystery',2,0,'no',0,'sherlock holmes, detective, london, watson','The first mystery featuring Sherlock Holmes and Dr. John Watson investigating a curious London murder.'),(74,'All the Light We Cannot See','Historical Fiction',2,0,'no',0,'wwii, france, radio, pulitzer, historical','A blind French girl and a German radio prodigy cross paths in occupied France during World War II.'),(75,'Where the Crawdads Sing','Mystery',2,0,'no',0,'marsh, murder mystery, nature, survival, drama','A resilient young girl raised alone in the North Carolina marshes becomes a suspect in a local murder.'),(76,'The Song of Achilles','Romance',2,0,'no',0,'mythology, trojan war, greece, achilles, epic','A reimagining of the Iliad centered on the deep relationship between Achilles and Patroclus.');
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `borrow_histories`
--

DROP TABLE IF EXISTS `borrow_histories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrow_histories` (
  `history_id` int NOT NULL AUTO_INCREMENT,
  `member_id` int NOT NULL,
  `member_name` varchar(45) DEFAULT NULL,
  `borrow_date` date NOT NULL,
  `original_return_date` date NOT NULL,
  `actual_return_date` date DEFAULT NULL,
  `late_days` int DEFAULT '0',
  `copy_id` int NOT NULL,
  PRIMARY KEY (`history_id`),
  KEY `member_id_idx` (`member_id`),
  KEY `copy_id_idx` (`copy_id`),
  CONSTRAINT `fk_borrow_histories_copy_id` FOREIGN KEY (`copy_id`) REFERENCES `book_copies` (`copy_id`),
  CONSTRAINT `fk_borrow_histories_member_id` FOREIGN KEY (`member_id`) REFERENCES `members` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=413 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrow_histories`
--

LOCK TABLES `borrow_histories` WRITE;
/*!40000 ALTER TABLE `borrow_histories` DISABLE KEYS */;
INSERT INTO `borrow_histories` VALUES (402,101,'Omri Spitzer','2026-09-01','2026-09-15','2026-09-15',0,1),(403,101,'Omri Spitzer','2026-09-01','2026-09-15','2026-09-24',0,13),(404,104,'Guy Shargorodsky','2026-09-05','2026-09-19','2026-09-19',0,14),(405,105,'Adan Ibrahim','2026-09-11','2026-09-24','2026-09-26',0,5),(406,103,'Yarden Nahum','2026-09-11','2026-09-24','2026-09-26',0,7),(407,109,'Shahar Dov','2026-09-12','2026-09-25','2026-09-25',0,11),(408,108,'Nir Levi','2026-09-15','2026-09-29',NULL,0,2),(409,104,'Guy Shargorodsky','2026-09-26','2026-10-10',NULL,0,12),(410,104,'Guy Shargorodsky','2026-09-15','2026-09-29',NULL,0,19),(411,104,'Guy Shargorodsky','2026-09-26','2026-10-10',NULL,0,20),(412,109,'Shahar Dov','2026-09-25','2026-10-09',NULL,0,17);
/*!40000 ALTER TABLE `borrow_histories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `borrow_tracking`
--

DROP TABLE IF EXISTS `borrow_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrow_tracking` (
  `tracking_date` date NOT NULL,
  `borrow_count` int DEFAULT NULL,
  `late_count` int DEFAULT NULL,
  PRIMARY KEY (`tracking_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrow_tracking`
--

LOCK TABLES `borrow_tracking` WRITE;
/*!40000 ALTER TABLE `borrow_tracking` DISABLE KEYS */;
INSERT INTO `borrow_tracking` VALUES ('2026-09-01',3,0),('2026-09-02',3,0),('2026-09-03',3,0),('2026-09-04',3,0),('2026-09-05',4,0),('2026-09-06',4,0),('2026-09-07',4,0),('2026-09-08',4,0),('2026-09-09',4,0),('2026-09-10',4,0),('2026-09-11',6,0),('2026-09-12',7,0),('2026-09-13',7,0),('2026-09-14',7,0),('2026-09-15',7,0),('2026-09-16',7,1),('2026-09-17',7,1),('2026-09-18',7,1),('2026-09-19',6,1),('2026-09-20',7,1),('2026-09-21',8,1),('2026-09-22',8,1),('2026-09-23',8,1),('2026-09-24',7,1),('2026-09-25',7,2),('2026-09-26',6,1),('2026-09-27',5,1),('2026-09-28',5,1);
/*!40000 ALTER TABLE `borrow_tracking` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `borrowed_books`
--

DROP TABLE IF EXISTS `borrowed_books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrowed_books` (
  `member_id` int NOT NULL,
  `borrow_date` date DEFAULT NULL,
  `return_date` date DEFAULT NULL,
  `librarian_name` varchar(255) DEFAULT NULL,
  `librarian_id` int DEFAULT NULL,
  `copy_id` int NOT NULL,
  `extension_date` date DEFAULT NULL,
  `book_id` int DEFAULT NULL,
  PRIMARY KEY (`member_id`,`copy_id`),
  KEY `member_id_idx` (`member_id`),
  KEY `copy_id_idx` (`copy_id`),
  KEY `book_id_idx` (`book_id`),
  CONSTRAINT `fk_borrowed_books_copy_id` FOREIGN KEY (`copy_id`) REFERENCES `book_copies` (`copy_id`),
  CONSTRAINT `fk_borrowed_books_member_id` FOREIGN KEY (`member_id`) REFERENCES `members` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrowed_books`
--

LOCK TABLES `borrowed_books` WRITE;
/*!40000 ALTER TABLE `borrowed_books` DISABLE KEYS */;
INSERT INTO `borrowed_books` VALUES (104,'2026-09-26','2026-10-10','Omri Spitzer',1,12,NULL,6),(104,'2026-09-26','2026-10-10','Omri Spitzer',1,19,NULL,10),(104,'2026-09-26','2026-10-10','Omri Spitzer',1,20,NULL,10),(108,'2026-09-15','2026-09-29','Omri Spitzer',1,2,NULL,1),(109,'2026-09-25','2026-10-09','Omri Spitzer',1,17,NULL,9);
/*!40000 ALTER TABLE `borrowed_books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `invoices`
--

DROP TABLE IF EXISTS `invoices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `invoices` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `member_id` int NOT NULL,
  `username` varchar(255) NOT NULL,
  `member_name` varchar(255) DEFAULT NULL,
  `subject` enum('General','Extension') NOT NULL,
  `content` varchar(255) DEFAULT NULL,
  `message_date` date NOT NULL,
  `is_read` enum('Read','notRead') DEFAULT NULL,
  PRIMARY KEY (`message_id`),
  KEY `member_id_idx` (`member_id`),
  CONSTRAINT `fk_invoices_member_id` FOREIGN KEY (`member_id`) REFERENCES `members` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `invoices`
--

LOCK TABLES `invoices` WRITE;
/*!40000 ALTER TABLE `invoices` DISABLE KEYS */;
/*!40000 ALTER TABLE `invoices` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `librarians`
--

DROP TABLE IF EXISTS `librarians`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `librarians` (
  `librarian_id` int NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `is_logged_in` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`librarian_id`),
  UNIQUE KEY `email_address` (`email_address`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `librarians`
--

LOCK TABLES `librarians` WRITE;
/*!40000 ALTER TABLE `librarians` DISABLE KEYS */;
INSERT INTO `librarians` VALUES (1,'Omri Spitzer','0587577242','omrisimo1@gmail.com','omri','123',0),(2,'Yarden Nahum','0503077993','yardennahum2@gmail.com','yarden','123',0),(3,'Stav Avraham','0505650800','stav.a2008@gmail.com','stava','123',0),(4,'Guy Shargorodsky','0522970633','shargo501@gmail.com','guy','123',0),(5,'Adan Ibrahim','0507777538','aden.ibrahim@e.braude.ac.il','adan','123',0);
/*!40000 ALTER TABLE `librarians` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `member_status_changes`
--

DROP TABLE IF EXISTS `member_status_changes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `member_status_changes` (
  `history_id` int NOT NULL AUTO_INCREMENT,
  `member_id` int DEFAULT NULL,
  `member_name` varchar(45) DEFAULT NULL,
  `status` enum('Frozen','NotFrozen') DEFAULT NULL,
  `change_date` date DEFAULT NULL,
  PRIMARY KEY (`history_id`),
  KEY `member_id_idx` (`member_id`),
  CONSTRAINT `fk_member_status_changes_member_id` FOREIGN KEY (`member_id`) REFERENCES `members` (`member_id`)
) ENGINE=InnoDB AUTO_INCREMENT=725 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `member_status_changes`
--

LOCK TABLES `member_status_changes` WRITE;
/*!40000 ALTER TABLE `member_status_changes` DISABLE KEYS */;
INSERT INTO `member_status_changes` VALUES (713,101,'Omri Spitzer','Frozen','2026-09-23'),(714,110,'Tony Curtis','NotFrozen','2026-09-07'),(715,107,'Michelle Davis','NotFrozen','2026-09-03'),(716,101,'Omri Spitzer','NotFrozen','2026-09-01'),(717,102,'Stav Avraham','NotFrozen','2026-09-01'),(718,103,'Yarden Nahum','NotFrozen','2026-09-01'),(719,104,'Guy Shargorodsky','NotFrozen','2026-09-01'),(720,105,'Adan Ibrahim','NotFrozen','2026-09-01'),(721,106,'David Smith','NotFrozen','2026-09-01'),(722,108,'Nir Levi','NotFrozen','2026-09-01'),(723,109,'Shahar Dov','NotFrozen','2026-09-01'),(724,107,'Michelle Davis','Frozen','2026-09-01');
/*!40000 ALTER TABLE `member_status_changes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `members`
--

DROP TABLE IF EXISTS `members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `members` (
  `member_id` int NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `freeze_status` enum('Frozen','NotFrozen') DEFAULT NULL,
  `freeze_date` date DEFAULT NULL,
  `email_address` varchar(255) DEFAULT NULL,
  `phone_number` varchar(255) DEFAULT NULL,
  `reader_card_barcode` varchar(255) DEFAULT NULL,
  `is_logged_in` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`member_id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email_address` (`email_address`),
  UNIQUE KEY `reader_card_barcode` (`reader_card_barcode`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `members`
--

LOCK TABLES `members` WRITE;
/*!40000 ALTER TABLE `members` DISABLE KEYS */;
INSERT INTO `members` VALUES (101,'Omri Spitzer','omrisimo1','123','Frozen','2026-09-23','omrisimo1@gmail.com','0587577241','MTAxfE9tcmkgU3BpdHplcg==',0),(102,'Stav Avraham','stav1','123','NotFrozen',NULL,'stav.a2008@gmail.com','0522222222','MTAyfFN0YXYgQXZyYWhhbQ==',0),(103,'Yarden Nahum','yarden','123','NotFrozen',NULL,'yardennahum2@gmail.com','0526532245','MTAzfFlhcmRlbiBOYWh1bQ==',0),(104,'Guy Shargorodsky','guy','123','NotFrozen',NULL,'shargo501@gmail.com','05000000000','MTA0fEd1eSBTaGFyZ29kc2t5',0),(105,'Adan Ibrahim','adan','123','NotFrozen',NULL,'adan@gmail.com','0544444444','MTA1fEFkYW4gSWJyYWhpbQ==',0),(106,'David Smith','david','123','NotFrozen',NULL,'david.smith@gmail.com','0577777777','MTA2fERhdmlkIFNtaXRo',0),(107,'Michelle Davis','michelle1','123','NotFrozen','2026-09-03','michelle1@gmail.com','0566666666','MTA3fE1pY2hlbGxlIERhdmlz',0),(108,'Nir Levi','nir','123','NotFrozen',NULL,'nir.levi@gmail.com','0599999999','MTA4fE5pciBMZXZp',0),(109,'Shahar Dov','Shahar','123','NotFrozen',NULL,'shahar@gmail.com','02022222','MTIzNHxzaHVoaQ==',0),(110,'Tony Curtis','tony','123','NotFrozen',NULL,'tony@gmail.com','0540587245','MTEwfFRvbnkgQ3VydGlz',0);
/*!40000 ALTER TABLE `members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `order_id` int NOT NULL,
  `book_id` int DEFAULT NULL,
  `order_date` date NOT NULL,
  `member_id` int NOT NULL,
  `member_name` varchar(255) DEFAULT NULL,
  `member_phone` varchar(255) DEFAULT NULL,
  `member_email` varchar(255) DEFAULT NULL,
  `arrival_status` enum('Arrived','notArrived') DEFAULT NULL,
  `arrival_date` date DEFAULT NULL,
  KEY `member_id_idx` (`member_id`),
  KEY `book_id_idx` (`book_id`),
  CONSTRAINT `fk_orders_book_id` FOREIGN KEY (`book_id`) REFERENCES `books` (`book_id`),
  CONSTRAINT `fk_orders_member_id` FOREIGN KEY (`member_id`) REFERENCES `members` (`member_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (15,10,'2026-09-27',103,'Yarden Nahum','0526532245','yardennahum2@gmail.com','notArrived',NULL),(16,10,'2026-09-27',104,'Guy Shargorodsky','05000000000','shargo501@gmail.com','notArrived',NULL),(17,6,'2026-09-20',105,'Adan Ibrahim','0544444444','adan@gmail.com','Arrived','2026-09-25');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--

DROP TABLE IF EXISTS `reports`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reports` (
  `report_date` date NOT NULL,
  `report_type` enum('memberStatusReport','borrowReport','statusTracking','borrowTracking') NOT NULL,
  `report_data` text,
  PRIMARY KEY (`report_date`,`report_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
INSERT INTO `reports` VALUES ('2026-07-31','memberStatusReport','member_id,member_name,status,change_date\n      107, Michelle Davis, NotFrozen, 2026-07-01\n  \n      108, Nir Levi, Frozen, 2026-07-01\n  \n      109, Shahar Dov, NotFrozen, 2026-07-01\n  \n      103, Yarden Nahum, NotFrozen, 2026-07-01\n  \n      106, David Smith, NotFrozen, 2026-07-05\n  \n      107, Michelle Davis, Frozen, 2026-07-15\n  \n      109, Shahar Dov, Frozen, 2026-07-20\n  \n      108, Nir Levi, NotFrozen, 2026-07-25\n  \n  103,Yarden Nahum,NotFrozen,2026-07-27'),('2026-07-31','borrowReport','member_id,member_name,book_title,borrow_date,original_return_date,actual_return_date,late_days,book_id\n   2, Omri Spitzer, Introduction to SQL, 2026-07-06, 2026-07-30, 2026-07-30, 0, 101\n   2, Omri Spitzer, Advanced Java, 2026-07-06, 2026-07-16, 2026-07-16, 0, 301\n   2, Michelle Davis, Advanced Java, 2026-07-06, 2026-07-20, 2026-07-21, 0, 303\n   3, Guy Shargorodsky, Introduction to SQL, 2026-07-06, 2026-07-20, 2026-07-25, 0, 103\n   3, Adan Ibrahim, Learn Python, 2026-07-06, 2026-07-20, 2026-07-20, 0, 202\n   4, Stav Avraham, Learn Python, 2026-07-06, 2026-07-20, 2026-07-20, 0, 201\n   4, Michelle Davis, Advanced Java, 2026-07-06, 2026-07-20, 2026-07-21, 0, 302\n   6, Shahar Dov, Introduction to SQL, 2026-07-06, 2026-07-20, 2026-07-29, 0, 102'),('2026-07-31','statusTracking','tracking_date,frozen_members,not_frozen_members\n \n 2026-07-01, 1, 3\n \n 2026-07-02, 1, 3\n \n 2026-07-03, 1, 3\n \n 2026-07-04, 1, 3\n \n 2026-07-05, 1, 4\n \n 2026-07-06, 1, 4\n \n 2026-07-07, 1, 4\n \n 2026-07-08, 1, 4\n \n 2026-07-09, 1, 4\n \n 2026-07-10, 1, 4\n \n 2026-07-11, 1, 4\n \n 2026-07-12, 1, 4\n \n 2026-07-13, 1, 4\n \n 2026-07-14, 1, 4\n \n 2026-07-15, 2, 3\n \n 2026-07-16, 2, 3\n \n 2026-07-17, 2, 3\n \n 2026-07-18, 2, 3\n \n 2026-07-19, 2, 3\n \n 2026-07-20, 3, 2\n \n 2026-07-21, 3, 2\n \n 2026-07-22, 3, 2\n \n 2026-07-23, 3, 2\n \n 2026-07-24, 3, 2\n \n 2026-07-25, 2, 3\n \n 2026-07-26, 2, 3\n \n 2026-07-27, 2, 4\n \n 2026-07-28, 2, 4\n \n 2026-07-29, 2, 4\n \n 2026-07-30, 2, 4\n  '),('2026-07-31','borrowTracking','tracking_date,borrow_count,late_count\n  2026-07-01, 0, 0\n  2026-07-02, 0, 0\n  2026-07-03, 0, 0\n  2026-07-04, 0, 0\n  2026-07-05, 0, 0\n  2026-07-06, 8, 0\n  2026-07-07, 8, 0\n  2026-07-08, 8, 0\n  2026-07-09, 8, 0\n  2026-07-10, 8, 0\n  2026-07-11, 8, 0\n  2026-07-12, 8, 0\n  2026-07-13, 8, 0\n  2026-07-14, 8, 0\n  2026-07-15, 8, 0\n  2026-07-16, 7, 0\n  2026-07-17, 7, 0\n  2026-07-18, 7, 0\n  2026-07-19, 7, 0\n  2026-07-20, 5, 0\n  2026-07-21, 3, 4\n  2026-07-22, 3, 2\n  2026-07-23, 3, 2\n  2026-07-24, 3, 2\n  2026-07-25, 2, 1\n  2026-07-26, 2, 1\n  2026-07-27, 2, 1\n  2026-07-28, 2, 1\n  2026-07-29, 1, 0\n  2026-07-30, 0, 0'),('2026-08-31','memberStatusReport','member_id,member_name,status,change_date\n101, Omri Spitzer, NotFrozen, 2026-08-01\n103, Yarden Nahum, NotFrozen, 2026-08-01\n104, Guy Shargodsky, NotFrozen, 2026-08-01\n105, Adan Ibrahim, NotFrozen, 2026-08-01\n107, Michelle Davis, NotFrozen, 2026-08-01\n108, Nir Levi, NotFrozen, 2026-08-01\n102, Stav Avraham, NotFrozen, 2026-08-02\n107, Michelle Davis, Frozen, 2026-08-03\n103, Yarden Nahum, Frozen, 2026-08-08\n103, Yarden Nahum, NotFrozen, 2026-08-11\n106, David Smith, NotFrozen, 2026-08-14\n109, Shahar Dov, NotFrozen, 2026-08-14\n108, Nir Levi, Frozen, 2026-08-16\n104, Guy Shargodsky, Frozen, 2026-08-16\n108, Nir Levi, NotFrozen, 2026-08-22\n104, Guy Shargodsky, NotFrozen, 2026-08-25\n'),('2026-08-31','borrowReport','member_id,member_name,book_title,borrow_date,original_return_date,actual_return_date,late_days,copy_id\n101, Omri Spitzer, To Kill a Mockingbird, 2026-07-30, 2026-08-14, null, 0, 1\n101, Omri Spitzer, A Brief History of Time, 2026-08-01, 2026-08-15, null, 0, 3\n101, Omri Spitzer, The Republic, 2026-08-20, 2026-08-30, null, 0, 15\n102, Stav Avraham, To Kill a Mockingbird, 2026-08-03, 2026-08-17, null, 0, 2\n102, Stav Avraham, A Brief History of Time, 2026-08-02, 2026-08-16, null, 0, 3\n102, Stav Avraham, To Kill a Mockingbird, 2026-08-04, 2026-08-21, null, 0, 1\n104, Guy Shargodsky, Sapiens A Brief History of Humankind, 2026-08-08, 2026-08-22, null, 0, 5\n104, Guy Shargodsky, Clean Code, 2026-08-06, 2026-08-20, null, 0, 7\n104, Guy Shargodsky, Mathematics Its Content Methods and Meaning, 2026-08-07, 2026-08-21, null, 0, 9\n105, Adan Ibrahim, Clean Code, 2026-08-09, 2026-08-23, null, 0, 8\n105, Adan Ibrahim, The Story of Art, 2026-08-10, 2026-08-24, null, 0, 13\n105, Adan Ibrahim, 1984, 2026-08-11, 2026-08-25, null, 0, 19\n105, Adan Ibrahim, The Republic, 2026-07-29, 2026-08-12, 2026-08-15, 0, 13\n105, Adan Ibrahim, A Brief History of Time, 2026-07-29, 2026-08-12, 2026-08-12, 0, 19\n'),('2026-08-31','statusTracking','tracking_date,frozen_members,not_frozen_members\n 2026-08-01, 0, 6\n 2026-08-01, 0, 7\n 2026-08-02, 1, 6\n 2026-08-03, 1, 6\n 2026-08-04, 1, 6\n 2026-08-05, 1, 6\n 2026-08-06, 1, 6\n 2026-08-07, 2, 5\n 2026-08-08, 2, 5\n 2026-08-09, 2, 5\n 2026-08-10, 1, 6\n 2026-08-11, 1, 6\n 2026-08-12, 1, 6\n 2026-08-13, 1, 8\n 2026-08-14, 1, 8\n 2026-08-15, 3, 6\n 2026-08-16, 3, 6\n 2026-08-17, 3, 6\n 2026-08-18, 3, 6\n 2026-08-19, 3, 6\n 2026-08-20, 3, 6\n 2026-08-21, 2, 7\n 2026-08-22, 2, 7\n 2026-08-23, 2, 7\n 2026-08-24, 1, 8\n 2026-08-25, 1, 8\n 2026-08-26, 1, 8\n 2026-08-27, 1, 8\n 2026-08-28, 1, 8\n 2026-08-29, 1, 8\n 2026-08-30, 1, 8\n  2026-08-31, 1, 8'),('2026-08-31','borrowTracking','\ntracking_date,borrow_count,late_count\n 2026-08-01, 3, 0\n 2026-08-02, 4, 0\n 2026-08-03, 5, 0\n 2026-08-04, 6, 0\n 2026-08-05, 7, 0\n 2026-08-06, 7, 0\n 2026-08-07, 8, 0\n 2026-08-08, 8, 0\n 2026-08-09, 9, 0\n 2026-08-10, 10, 0\n 2026-08-11, 11, 0\n 2026-08-12, 12, 0\n 2026-08-13, 11, 0\n 2026-08-14, 11, 1\n 2026-08-15, 11, 1\n 2026-08-16, 10, 1\n 2026-08-17, 10, 2\n 2026-08-18, 10, 3\n 2026-08-19, 10, 4\n 2026-08-20, 10, 4\n 2026-08-21, 11, 4\n 2026-08-22, 11, 5\n 2026-08-23, 11, 7\n 2026-08-24, 11, 8\n 2026-08-25, 11, 9\n 2026-08-26, 11, 10\n 2026-08-27, 11, 10\n 2026-08-28, 11, 10\n 2026-08-29, 11, 10\n 2026-08-30, 11, 10\n 2026-08-31, 11, 10\n ');
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_tracking`
--

DROP TABLE IF EXISTS `status_tracking`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `status_tracking` (
  `tracking_date` date NOT NULL,
  `frozen_members` int DEFAULT NULL,
  `not_frozen_members` int DEFAULT NULL,
  PRIMARY KEY (`tracking_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_tracking`
--

LOCK TABLES `status_tracking` WRITE;
/*!40000 ALTER TABLE `status_tracking` DISABLE KEYS */;
INSERT INTO `status_tracking` VALUES ('2026-09-01',1,8),('2026-09-02',1,8),('2026-09-03',0,9),('2026-09-04',0,9),('2026-09-05',0,9),('2026-09-06',0,9),('2026-09-07',0,10),('2026-09-08',0,10),('2026-09-09',0,10),('2026-09-10',0,10),('2026-09-11',0,10),('2026-09-12',0,10),('2026-09-13',0,10),('2026-09-14',0,10),('2026-09-15',0,10),('2026-09-16',0,10),('2026-09-17',0,10),('2026-09-18',0,10),('2026-09-19',0,10),('2026-09-20',0,10),('2026-09-21',0,10),('2026-09-22',0,10),('2026-09-23',1,9),('2026-09-24',1,9),('2026-09-25',1,9),('2026-09-26',1,9),('2026-09-27',1,9),('2026-09-28',1,9);
/*!40000 ALTER TABLE `status_tracking` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-10-06 22:16:13
