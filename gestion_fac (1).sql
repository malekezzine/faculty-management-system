-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 11, 2026 at 05:01 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gestion_fac`
--

-- --------------------------------------------------------

--
-- Table structure for table `absence`
--

CREATE TABLE `absence` (
  `id` int(11) NOT NULL,
  `etudiant_id` int(11) NOT NULL,
  `matiere_id` int(11) NOT NULL,
  `date_absence` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `absence`
--

INSERT INTO `absence` (`id`, `etudiant_id`, `matiere_id`, `date_absence`) VALUES
(19, 17, 2, '2026-05-05 00:00:00'),
(23, 19, 10, '2026-05-06 00:00:00'),
(27, 17, 3, '2026-05-06 00:00:00');

-- --------------------------------------------------------

--
-- Table structure for table `emploi`
--

CREATE TABLE `emploi` (
  `id` int(11) NOT NULL,
  `jour` varchar(20) NOT NULL,
  `heure` varchar(20) NOT NULL,
  `salle` int(11) NOT NULL,
  `groupe_id` int(11) NOT NULL,
  `matiere_id` int(11) NOT NULL,
  `enseignant_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `emploi`
--

INSERT INTO `emploi` (`id`, `jour`, `heure`, `salle`, `groupe_id`, `matiere_id`, `enseignant_id`) VALUES
(18, 'lundi', '10h', 5, 1, 6, 1),
(19, 'jeudi', '8h', 6, 1, 3, 20),
(20, 'lundi', '5h', 7, 1, 2, 21),
(21, 'jeudi', '5h', 7, 2, 2, 21),
(22, 'lundi', '14', 7, 2, 9, 22),
(23, 'jeudi', '14', 1, 1, 10, 23),
(24, 'vendredi', '2h', 2, 1, 11, 23),
(25, 'mardi', '12', 2, 2, 3, 20);

-- --------------------------------------------------------

--
-- Table structure for table `enseignant`
--

CREATE TABLE `enseignant` (
  `id` int(11) NOT NULL,
  `nom` varchar(20) NOT NULL,
  `prenom` varchar(20) NOT NULL,
  `specialite` varchar(20) NOT NULL,
  `matrice` varchar(50) NOT NULL DEFAULT '',
  `login` varchar(100) NOT NULL DEFAULT '',
  `mot_de_passe` varchar(50) NOT NULL DEFAULT 'prof123'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `enseignant`
--

INSERT INTO `enseignant` (`id`, `nom`, `prenom`, `specialite`, `matrice`, `login`, `mot_de_passe`) VALUES
(1, 'emelle', 'belhaj', 'français', '', 'belhaj.emelle', 'prof123'),
(18, 'emelle', 'belhaj', 'english', '', 'belhaj.emelle', 'prof123'),
(20, 'taysir', 'bou', 'info', '', 'bou.taysir', 'prof123'),
(21, 'safia', 'jsp', 'web', '', 'jsp.safia', 'prof123'),
(22, 'safia', 'rabaoui', 'web avancé', '', 'rabaoui.safia', 'prof123'),
(24, 'taysir', 'bouaskar', 'algo', '', 'bouaskar.taysir', 'prof123');

-- --------------------------------------------------------

--
-- Table structure for table `etudiant`
--

CREATE TABLE `etudiant` (
  `id` int(11) NOT NULL,
  `nom` varchar(20) NOT NULL,
  `prenom` varchar(20) NOT NULL,
  `cin` int(11) NOT NULL,
  `groupe_id` int(11) NOT NULL,
  `email` varchar(100) NOT NULL,
  `telephone` varchar(100) NOT NULL,
  `date_naissance` date NOT NULL,
  `adresse` varchar(100) NOT NULL,
  `niveau` varchar(50) NOT NULL DEFAULT '',
  `filiere` varchar(100) NOT NULL DEFAULT '',
  `login` varchar(50) DEFAULT NULL,
  `mot_de_passe` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `etudiant`
--

INSERT INTO `etudiant` (`id`, `nom`, `prenom`, `cin`, `groupe_id`, `email`, `telephone`, `date_naissance`, `adresse`, `niveau`, `filiere`, `login`, `mot_de_passe`) VALUES
(17, 'hamma', 'hr', 123456, 1, 'hamma@gmail.com', '24442455', '2000-01-01', 'nabeul', '', '', 'hr.hamma', '123456'),
(19, 'melek', 'ezzine', 123456, 1, 'melek@gmail.com', '24442455', '2005-01-01', 'nabeul', '', '', 'ezzine.melek', '123456'),
(20, 'yousef', 'brahim', 15366061, 2, 'yousef@gmail.com', '25634017', '2005-01-02', 'manzel tmin', '', '', 'brahim.yousef', '15366061'),
(21, 'yasmine', 'ez', 123456, 1, 'yasmine@gmail.com', '24442455', '2005-01-01', 'bk', '', '', 'ez.yasmine', '123456'),
(22, 'bentouila', 'doua', 123456, 2, 'douay@gmail.com', '24442455', '2005-01-01', 'daarchaabane', '', '', 'doua.bentouila', '123456'),
(23, 'mohamed', 'hajiri', 123456, 1, 'mohamed@gmail.com', '24442455', '2000-11-01', 'mrezga', '', '', 'hajiri.mohamed', '123456');

-- --------------------------------------------------------

--
-- Table structure for table `groupe`
--

CREATE TABLE `groupe` (
  `id` int(11) NOT NULL,
  `nom` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `groupe`
--

INSERT INTO `groupe` (`id`, `nom`) VALUES
(1, 'cpA'),
(2, 'cpB');

-- --------------------------------------------------------

--
-- Table structure for table `matiere`
--

CREATE TABLE `matiere` (
  `id` int(11) NOT NULL,
  `nom` varchar(20) NOT NULL,
  `code` varchar(20) NOT NULL DEFAULT '',
  `coefficient` double NOT NULL,
  `volume_horaire` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `matiere`
--

INSERT INTO `matiere` (`id`, `nom`, `code`, `coefficient`, `volume_horaire`) VALUES
(2, 'web', '', 4, 0),
(3, 'conception', '', 4, 0),
(6, 'english', '', 2, 0),
(7, 'francais', '', 2, 0),
(8, 'web2', '', 4, 0),
(9, 'web avancé', '', 4, 0),
(10, 'concept', '', 6, 0),
(11, 'algo', '', 8, 0);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `absence`
--
ALTER TABLE `absence`
  ADD PRIMARY KEY (`id`),
  ADD KEY `etudiant_id` (`etudiant_id`),
  ADD KEY `matiere_id` (`matiere_id`);

--
-- Indexes for table `emploi`
--
ALTER TABLE `emploi`
  ADD PRIMARY KEY (`id`),
  ADD KEY `groupe_id` (`groupe_id`),
  ADD KEY `emploi_ibfk_1` (`enseignant_id`),
  ADD KEY `matiere_id` (`matiere_id`);

--
-- Indexes for table `enseignant`
--
ALTER TABLE `enseignant`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `etudiant`
--
ALTER TABLE `etudiant`
  ADD PRIMARY KEY (`id`),
  ADD KEY `groupe_id` (`groupe_id`);

--
-- Indexes for table `groupe`
--
ALTER TABLE `groupe`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `matiere`
--
ALTER TABLE `matiere`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `absence`
--
ALTER TABLE `absence`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=28;

--
-- AUTO_INCREMENT for table `emploi`
--
ALTER TABLE `emploi`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `enseignant`
--
ALTER TABLE `enseignant`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `etudiant`
--
ALTER TABLE `etudiant`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `groupe`
--
ALTER TABLE `groupe`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `matiere`
--
ALTER TABLE `matiere`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `absence`
--
ALTER TABLE `absence`
  ADD CONSTRAINT `absence_ibfk_1` FOREIGN KEY (`etudiant_id`) REFERENCES `etudiant` (`id`),
  ADD CONSTRAINT `absence_ibfk_2` FOREIGN KEY (`matiere_id`) REFERENCES `matiere` (`id`);

--
-- Constraints for table `emploi`
--
ALTER TABLE `emploi`
  ADD CONSTRAINT `emploi_ibfk_1` FOREIGN KEY (`matiere_id`) REFERENCES `matiere` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `etudiant`
--
ALTER TABLE `etudiant`
  ADD CONSTRAINT `etudiant_ibfk_1` FOREIGN KEY (`groupe_id`) REFERENCES `groupe` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
