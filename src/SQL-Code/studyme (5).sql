-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 08. Aug 2017 um 09:18
-- Server-Version: 10.1.25-MariaDB
-- PHP-Version: 7.1.7

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `studyme`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `author`
--

CREATE TABLE `author` (
  `ID_person` int(11) NOT NULL,
  `ID_study` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_creator` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `author`
--

INSERT INTO `author` (`ID_person`, `ID_study`, `time`, `ID_creator`) VALUES
(1, 2, '2017-07-28 21:54:49', 1),
(1, 4, '2017-07-28 22:11:58', 1),
(1, 5, '2017-08-06 09:51:52', 1),
(1, 6, '2017-08-06 09:52:56', 1),
(1, 7, '2017-08-06 09:53:36', 1),
(1, 8, '2017-08-07 10:18:38', 1),
(1, 9, '2017-08-07 10:27:27', 1),
(21, 2, '2017-07-30 23:30:47', 1),
(23, 2, '2017-07-30 23:32:13', 21),
(24, 2, '2017-08-01 12:20:19', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `channel`
--

CREATE TABLE `channel` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` tinytext NOT NULL,
  `unit` tinytext NOT NULL,
  `ID_study` int(11) NOT NULL,
  `ID_creator` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `channel`
--

INSERT INTO `channel` (`ID`, `time`, `name`, `unit`, `ID_study`, `ID_creator`) VALUES
(4, '2017-07-29 07:49:59', 'Test Channel', '', 4, 1),
(13, '2017-07-30 17:16:28', 'ChannelTest', 'cm', 2, 1),
(14, '2017-08-07 10:29:54', 'channel1', 'C', 8, 1),
(15, '2017-08-07 10:35:10', 'channel2', 'Mol/L', 8, 1),
(16, '2017-08-07 10:37:18', 'channel3', 'Mol/L', 8, 1),
(17, '2017-08-07 10:37:58', 'channel4', 'C', 8, 1),
(18, '2017-08-07 10:38:46', 'channel5', 'Mol/L', 8, 1),
(19, '2017-08-07 10:39:47', 'channel6', 'Mol/L', 8, 1),
(20, '2017-08-07 10:40:47', '?', '?', 9, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `data`
--

CREATE TABLE `data` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ID_channel` int(11) NOT NULL,
  `data_time` timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `data_value` double NOT NULL,
  `ID_creator` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `data`
--

INSERT INTO `data` (`ID`, `time`, `ID_channel`, `data_time`, `data_value`, `ID_creator`) VALUES
(7, '2017-07-30 22:50:34', 13, '2017-07-30 22:50:34.822475', 1.23, 1),
(8, '2017-07-30 22:51:02', 13, '2017-07-30 22:51:02.952861', 2.345, 1),
(9, '2017-07-30 23:10:37', 13, '2017-07-30 22:10:15.000000', 5.9786, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `person`
--

CREATE TABLE `person` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `username` tinytext NOT NULL,
  `password` tinytext NOT NULL,
  `name` tinytext NOT NULL,
  `email` tinytext NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `person`
--

INSERT INTO `person` (`ID`, `time`, `username`, `password`, `name`, `email`) VALUES
(1, '2017-07-26 21:38:06', 'Nils', '1234', 'Nils Wandel', 'nils.wandel@epfl.ch'),
(21, '2017-07-30 23:16:16', 'Nils2', '1234', 'Nils Wandel Test Account 2', 'blubb@roche.ch'),
(23, '2017-07-30 23:20:11', 'Nils3', '1234', 'zdrjpic', 'trshdn'),
(24, '2017-08-01 12:19:37', 'Krispin', '1234', 'Krispin Wandel', 'krispin@ethz.ch');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `study`
--

CREATE TABLE `study` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` text NOT NULL,
  `description` mediumtext,
  `ID_creator` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `study`
--

INSERT INTO `study` (`ID`, `time`, `name`, `description`, `ID_creator`) VALUES
(2, '2017-07-28 21:54:49', 'My Test Study', 'Das ist eine Test-Studie', 1),
(4, '2017-07-28 22:11:58', 'My Test Study 2', '', 1),
(5, '2017-08-06 09:51:52', 'Study A', '', 1),
(6, '2017-08-06 09:52:56', 'Study B', '', 1),
(7, '2017-08-06 09:53:36', 'Study C', '', 1),
(8, '2017-08-07 10:18:38', 'Study D', '', 1),
(9, '2017-08-07 10:27:27', 'Study D Query', '', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `tag`
--

CREATE TABLE `tag` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` tinytext NOT NULL,
  `ID_creator` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `tag`
--

INSERT INTO `tag` (`ID`, `time`, `name`, `ID_creator`) VALUES
(1, '2017-07-28 15:45:30', 'TestTag1', 12),
(2, '2017-07-28 15:47:05', 'TestTag2', 14),
(3, '2017-07-28 15:57:14', 'TestTag3', 16),
(4, '2017-08-06 09:51:57', 'A', 1),
(5, '2017-08-06 09:52:09', 'B', 1),
(6, '2017-08-06 09:52:18', 'C', 1),
(7, '2017-08-07 10:18:47', 'male', 1),
(8, '2017-08-07 10:18:51', 'gender', 1),
(9, '2017-08-07 10:18:55', 'female', 1),
(10, '2017-08-07 10:19:09', 'Object', 1),
(11, '2017-08-07 10:19:20', 'species', 1),
(12, '2017-08-07 10:19:25', 'mammal', 1),
(13, '2017-08-07 10:19:29', 'dog', 1),
(14, '2017-08-07 10:19:41', 'measurement', 1),
(15, '2017-08-07 10:19:44', 'channel', 1),
(16, '2017-08-07 10:20:00', 'medication', 1),
(17, '2017-08-07 10:20:18', 'temperature', 1),
(18, '2017-08-07 10:20:24', 'substance', 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `tag_channel`
--

CREATE TABLE `tag_channel` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_tag` int(11) NOT NULL,
  `ID_channel` int(11) NOT NULL,
  `ID_creator` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `tag_channel`
--

INSERT INTO `tag_channel` (`ID`, `time`, `ID_tag`, `ID_channel`, `ID_creator`) VALUES
(1, '2017-08-07 10:33:37', 34, 14, 1),
(2, '2017-08-07 10:33:57', 28, 14, 1),
(5, '2017-08-07 10:36:31', 28, 15, 1),
(6, '2017-08-07 10:36:44', 35, 15, 1),
(7, '2017-08-07 10:37:33', 28, 16, 1),
(8, '2017-08-07 10:37:39', 37, 16, 1),
(9, '2017-08-07 10:38:06', 29, 17, 1),
(10, '2017-08-07 10:38:13', 34, 17, 1),
(12, '2017-08-07 10:39:04', 29, 18, 1),
(13, '2017-08-07 10:39:09', 35, 18, 1),
(14, '2017-08-07 10:40:01', 29, 19, 1),
(15, '2017-08-07 10:40:05', 37, 19, 1),
(16, '2017-08-07 10:41:05', 41, 20, 1),
(17, '2017-08-07 10:41:11', 44, 20, 1);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `tag_pointer`
--

CREATE TABLE `tag_pointer` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_tag` int(11) NOT NULL,
  `ID_creator` int(11) NOT NULL,
  `ID_study` int(11) NOT NULL,
  `viewX` double DEFAULT NULL,
  `viewY` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `tag_pointer`
--

INSERT INTO `tag_pointer` (`ID`, `time`, `ID_tag`, `ID_creator`, `ID_study`, `viewX`, `viewY`) VALUES
(5, '2017-07-29 07:50:32', 1, 1, 4, NULL, NULL),
(11, '2017-08-01 08:15:26', 2, 21, 2, NULL, NULL),
(12, '2017-08-01 08:16:26', 1, 21, 2, NULL, NULL),
(13, '2017-08-01 08:16:37', 3, 21, 2, NULL, NULL),
(14, '2017-08-06 09:51:57', 4, 1, 5, NULL, NULL),
(15, '2017-08-06 09:52:09', 5, 1, 5, NULL, NULL),
(16, '2017-08-06 09:52:12', 5, 1, 5, NULL, NULL),
(17, '2017-08-06 09:52:18', 6, 1, 5, NULL, NULL),
(18, '2017-08-06 09:53:06', 4, 1, 6, NULL, NULL),
(19, '2017-08-06 09:53:10', 5, 1, 6, NULL, NULL),
(20, '2017-08-06 09:53:13', 6, 1, 6, NULL, NULL),
(21, '2017-08-06 09:53:48', 4, 1, 7, NULL, NULL),
(22, '2017-08-06 09:53:51', 5, 1, 7, NULL, NULL),
(23, '2017-08-06 09:53:54', 6, 1, 7, NULL, NULL),
(24, '2017-08-07 10:18:47', 7, 1, 8, NULL, NULL),
(25, '2017-08-07 10:18:51', 8, 1, 8, NULL, NULL),
(26, '2017-08-07 10:18:55', 9, 1, 8, NULL, NULL),
(27, '2017-08-07 10:19:00', 8, 1, 8, NULL, NULL),
(28, '2017-08-07 10:19:09', 10, 1, 8, NULL, NULL),
(29, '2017-08-07 10:19:14', 10, 1, 8, NULL, NULL),
(30, '2017-08-07 10:19:20', 11, 1, 8, NULL, NULL),
(31, '2017-08-07 10:19:25', 12, 1, 8, NULL, NULL),
(32, '2017-08-07 10:19:29', 13, 1, 8, NULL, NULL),
(33, '2017-08-07 10:19:41', 14, 1, 8, NULL, NULL),
(34, '2017-08-07 10:19:44', 15, 1, 8, NULL, NULL),
(35, '2017-08-07 10:19:47', 15, 1, 8, NULL, NULL),
(36, '2017-08-07 10:20:00', 16, 1, 8, NULL, NULL),
(37, '2017-08-07 10:20:09', 15, 1, 8, NULL, NULL),
(38, '2017-08-07 10:20:18', 17, 1, 8, NULL, NULL),
(39, '2017-08-07 10:20:24', 18, 1, 8, NULL, NULL),
(41, '2017-08-07 10:27:36', 10, 1, 9, NULL, NULL),
(42, '2017-08-07 10:27:45', 11, 1, 9, NULL, NULL),
(43, '2017-08-07 10:27:51', 12, 1, 9, NULL, NULL),
(44, '2017-08-07 10:28:01', 15, 1, 9, NULL, NULL),
(45, '2017-08-07 10:28:10', 17, 1, 9, NULL, NULL),
(46, '2017-08-07 10:52:44', 15, 1, 9, NULL, NULL),
(48, '2017-08-07 11:10:55', 14, 1, 9, NULL, NULL);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `tag_tag`
--

CREATE TABLE `tag_tag` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_tag1` int(11) NOT NULL,
  `ID_tag2` int(11) NOT NULL,
  `ID_creator` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Daten für Tabelle `tag_tag`
--

INSERT INTO `tag_tag` (`ID`, `time`, `ID_tag1`, `ID_tag2`, `ID_creator`) VALUES
(7, '2017-08-01 08:17:26', 11, 13, 21),
(12, '2017-08-01 08:29:16', 11, 12, 21),
(13, '2017-08-06 09:52:33', 14, 15, 1),
(14, '2017-08-06 09:52:45', 16, 17, 1),
(15, '2017-08-06 09:53:22', 19, 20, 1),
(16, '2017-08-06 09:54:01', 21, 22, 1),
(17, '2017-08-06 09:54:15', 22, 23, 1),
(18, '2017-08-07 10:21:36', 24, 25, 1),
(19, '2017-08-07 10:22:33', 25, 28, 1),
(20, '2017-08-07 10:22:40', 28, 30, 1),
(21, '2017-08-07 10:22:50', 30, 31, 1),
(22, '2017-08-07 10:22:56', 31, 32, 1),
(23, '2017-08-07 10:23:07', 26, 27, 1),
(24, '2017-08-07 10:23:16', 27, 29, 1),
(25, '2017-08-07 10:23:41', 29, 30, 1),
(26, '2017-08-07 10:24:45', 33, 34, 1),
(27, '2017-08-07 10:25:57', 34, 38, 1),
(28, '2017-08-07 10:26:34', 33, 35, 1),
(29, '2017-08-07 10:26:43', 35, 39, 1),
(30, '2017-08-07 10:26:59', 36, 37, 1),
(31, '2017-08-07 10:27:07', 37, 39, 1),
(32, '2017-08-07 10:28:45', 41, 42, 1),
(33, '2017-08-07 10:28:50', 42, 43, 1),
(34, '2017-08-07 10:28:56', 44, 45, 1),
(35, '2017-08-07 11:11:11', 46, 48, 1);

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `author`
--
ALTER TABLE `author`
  ADD UNIQUE KEY `ID_person` (`ID_person`,`ID_study`),
  ADD KEY `ID_study` (`ID_study`);

--
-- Indizes für die Tabelle `channel`
--
ALTER TABLE `channel`
  ADD UNIQUE KEY `ID` (`ID`),
  ADD KEY `ID_study` (`ID_study`);

--
-- Indizes für die Tabelle `data`
--
ALTER TABLE `data`
  ADD UNIQUE KEY `ID` (`ID`),
  ADD KEY `ID_channel` (`ID_channel`);

--
-- Indizes für die Tabelle `person`
--
ALTER TABLE `person`
  ADD UNIQUE KEY `ID` (`ID`),
  ADD UNIQUE KEY `username` (`username`(50)) USING BTREE;

--
-- Indizes für die Tabelle `study`
--
ALTER TABLE `study`
  ADD UNIQUE KEY `ID` (`ID`),
  ADD UNIQUE KEY `name` (`name`(500));

--
-- Indizes für die Tabelle `tag`
--
ALTER TABLE `tag`
  ADD UNIQUE KEY `ID` (`ID`),
  ADD UNIQUE KEY `name` (`name`(50));

--
-- Indizes für die Tabelle `tag_channel`
--
ALTER TABLE `tag_channel`
  ADD UNIQUE KEY `ID` (`ID`),
  ADD UNIQUE KEY `ID_tag` (`ID_tag`,`ID_channel`),
  ADD KEY `ID_channel` (`ID_channel`);

--
-- Indizes für die Tabelle `tag_pointer`
--
ALTER TABLE `tag_pointer`
  ADD UNIQUE KEY `ID` (`ID`),
  ADD KEY `ID_tag` (`ID_tag`),
  ADD KEY `ID_study` (`ID_study`);

--
-- Indizes für die Tabelle `tag_tag`
--
ALTER TABLE `tag_tag`
  ADD UNIQUE KEY `ID` (`ID`),
  ADD UNIQUE KEY `ID_tag1` (`ID_tag1`,`ID_tag2`),
  ADD KEY `ID_tag2` (`ID_tag2`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `channel`
--
ALTER TABLE `channel`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;
--
-- AUTO_INCREMENT für Tabelle `data`
--
ALTER TABLE `data`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;
--
-- AUTO_INCREMENT für Tabelle `person`
--
ALTER TABLE `person`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;
--
-- AUTO_INCREMENT für Tabelle `study`
--
ALTER TABLE `study`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;
--
-- AUTO_INCREMENT für Tabelle `tag`
--
ALTER TABLE `tag`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;
--
-- AUTO_INCREMENT für Tabelle `tag_channel`
--
ALTER TABLE `tag_channel`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;
--
-- AUTO_INCREMENT für Tabelle `tag_pointer`
--
ALTER TABLE `tag_pointer`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;
--
-- AUTO_INCREMENT für Tabelle `tag_tag`
--
ALTER TABLE `tag_tag`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=36;
--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `author`
--
ALTER TABLE `author`
  ADD CONSTRAINT `author_ibfk_1` FOREIGN KEY (`ID_person`) REFERENCES `person` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `author_ibfk_2` FOREIGN KEY (`ID_study`) REFERENCES `study` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `channel`
--
ALTER TABLE `channel`
  ADD CONSTRAINT `channel_ibfk_1` FOREIGN KEY (`ID_study`) REFERENCES `study` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `data`
--
ALTER TABLE `data`
  ADD CONSTRAINT `data_ibfk_1` FOREIGN KEY (`ID_channel`) REFERENCES `channel` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `tag_channel`
--
ALTER TABLE `tag_channel`
  ADD CONSTRAINT `tag_channel_ibfk_1` FOREIGN KEY (`ID_channel`) REFERENCES `channel` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tag_channel_ibfk_2` FOREIGN KEY (`ID_tag`) REFERENCES `tag_pointer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `tag_pointer`
--
ALTER TABLE `tag_pointer`
  ADD CONSTRAINT `tag_pointer_ibfk_1` FOREIGN KEY (`ID_tag`) REFERENCES `tag` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tag_pointer_ibfk_2` FOREIGN KEY (`ID_study`) REFERENCES `study` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints der Tabelle `tag_tag`
--
ALTER TABLE `tag_tag`
  ADD CONSTRAINT `tag_tag_ibfk_1` FOREIGN KEY (`ID_tag1`) REFERENCES `tag_pointer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tag_tag_ibfk_2` FOREIGN KEY (`ID_tag2`) REFERENCES `tag_pointer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
