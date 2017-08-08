-- phpMyAdmin SQL Dump
-- version 4.7.0
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 27. Jul 2017 um 14:31
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

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `data`
--

CREATE TABLE `data` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ID_channel` int(11) NOT NULL,
  `data_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `data_value` double NOT NULL,
  `ID_creator` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
(1, '2017-07-26 21:38:06', 'Nils', '1234', 'Nils Wandel', 'nils.wandel@epfl.ch');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `study`
--

CREATE TABLE `study` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `name` text NOT NULL,
  `description` mediumtext NOT NULL,
  `ID_creator` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `tag_pointer`
--

CREATE TABLE `tag_pointer` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_tag` int(11) NOT NULL,
  `ID_creator` int(11) NOT NULL,
  `ID_study` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `tag_study`
--

CREATE TABLE `tag_study` (
  `ID` int(11) NOT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `ID_study` int(11) NOT NULL,
  `ID_tag` int(11) NOT NULL,
  `ID_creator` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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
  ADD UNIQUE KEY `name` (`name`(50),`ID_study`),
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
-- Indizes für die Tabelle `tag_study`
--
ALTER TABLE `tag_study`
  ADD UNIQUE KEY `ID` (`ID`),
  ADD UNIQUE KEY `ID_study` (`ID_study`,`ID_tag`),
  ADD KEY `ID_tag` (`ID_tag`);

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
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `data`
--
ALTER TABLE `data`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `person`
--
ALTER TABLE `person`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;
--
-- AUTO_INCREMENT für Tabelle `study`
--
ALTER TABLE `study`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `tag`
--
ALTER TABLE `tag`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `tag_channel`
--
ALTER TABLE `tag_channel`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `tag_pointer`
--
ALTER TABLE `tag_pointer`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `tag_study`
--
ALTER TABLE `tag_study`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT für Tabelle `tag_tag`
--
ALTER TABLE `tag_tag`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;
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
-- Constraints der Tabelle `tag_study`
--
ALTER TABLE `tag_study`
  ADD CONSTRAINT `tag_study_ibfk_1` FOREIGN KEY (`ID_study`) REFERENCES `study` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `tag_study_ibfk_2` FOREIGN KEY (`ID_tag`) REFERENCES `tag_pointer` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

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
