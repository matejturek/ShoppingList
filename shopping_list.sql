-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hostiteľ: 127.0.0.1
-- Čas generovania: Sun 07.Apr 2024, 12:27
-- Verzia serveru: 10.4.27-MariaDB
-- Verzia PHP: 8.0.25

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Databáza: `shopping_list`
--

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `categories`
--

CREATE TABLE `categories` (
  `categoryId` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `listId` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `categories`
--

INSERT INTO `categories` (`categoryId`, `name`, `listId`) VALUES
(13, 'Shirts', 8);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `invitations`
--

CREATE TABLE `invitations` (
  `invitationId` int(11) NOT NULL,
  `userId` int(11) DEFAULT NULL,
  `listId` int(11) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `invitations`
--

INSERT INTO `invitations` (`invitationId`, `userId`, `listId`, `status`) VALUES
(7, 12, 7, 1);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `items`
--

CREATE TABLE `items` (
  `itemId` int(11) NOT NULL,
  `listId` int(11) NOT NULL,
  `categoryId` int(11) DEFAULT NULL,
  `name` varchar(45) NOT NULL,
  `quantity` int(11) NOT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 0,
  `link` varchar(255) DEFAULT NULL,
  `shelf` varchar(45) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `items`
--

INSERT INTO `items` (`itemId`, `listId`, `categoryId`, `name`, `quantity`, `status`, `link`, `shelf`) VALUES
(31, 7, NULL, 'Milk', 1, 0, NULL, 'Dairy'),
(32, 7, NULL, 'Eggs', 1, 0, NULL, 'Dairy'),
(33, 7, NULL, 'Bread', 1, 0, NULL, 'Dairy'),
(35, 7, NULL, 'Bananas', 1, 0, NULL, 'Fruit'),
(36, 7, NULL, 'Spaghetti', 1, 0, NULL, NULL),
(44, 8, NULL, 'Black', 1, 0, NULL, NULL),
(46, 8, NULL, 'White', 1, 0, NULL, NULL),
(47, 8, NULL, 'Long', 1, 0, NULL, NULL),
(49, 8, NULL, 'Short', 1, 0, NULL, NULL);

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `lists`
--

CREATE TABLE `lists` (
  `listId` int(11) NOT NULL,
  `userId` int(11) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `created` timestamp NOT NULL DEFAULT current_timestamp(),
  `edited` timestamp NOT NULL DEFAULT current_timestamp(),
  `notes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `lists`
--

INSERT INTO `lists` (`listId`, `userId`, `name`, `created`, `edited`, `notes`) VALUES
(7, 11, 'Food', '2024-04-07 09:04:21', '2024-04-07 09:04:21', 'Daily food'),
(8, 12, 'Clothes', '2024-04-07 09:41:31', '2024-04-07 09:41:31', '');

-- --------------------------------------------------------

--
-- Štruktúra tabuľky pre tabuľku `users`
--

CREATE TABLE `users` (
  `userId` int(11) NOT NULL,
  `email` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `password` varchar(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `users`
--

INSERT INTO `users` (`userId`, `email`, `name`, `password`) VALUES
(11, 'mato.turek@gmail.com', 'matej-test1', '$2y$10$2LImJqY7ZutswWYJKZxW3OiTZ9FEpknP9Vxo34lr.u/XdTDDddR1W'),
(12, 'mato.turek2@gmail.com', 'matej2', '$2y$10$/SQuDALO.07nd1YZnarRU.W0mgL0SgNrUryzlnx4ydhzymIjx/Ipu');

--
-- Kľúče pre exportované tabuľky
--

--
-- Indexy pre tabuľku `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`categoryId`),
  ADD KEY `fk_categories_lists` (`listId`);

--
-- Indexy pre tabuľku `invitations`
--
ALTER TABLE `invitations`
  ADD PRIMARY KEY (`invitationId`),
  ADD KEY `userId` (`userId`),
  ADD KEY `listId` (`listId`);

--
-- Indexy pre tabuľku `items`
--
ALTER TABLE `items`
  ADD PRIMARY KEY (`itemId`),
  ADD KEY `fk_Item_List1` (`listId`),
  ADD KEY `fk_Item_Category1` (`categoryId`);

--
-- Indexy pre tabuľku `lists`
--
ALTER TABLE `lists`
  ADD PRIMARY KEY (`listId`),
  ADD KEY `userID` (`userId`);

--
-- Indexy pre tabuľku `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`userId`);

--
-- AUTO_INCREMENT pre exportované tabuľky
--

--
-- AUTO_INCREMENT pre tabuľku `categories`
--
ALTER TABLE `categories`
  MODIFY `categoryId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT pre tabuľku `invitations`
--
ALTER TABLE `invitations`
  MODIFY `invitationId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT pre tabuľku `items`
--
ALTER TABLE `items`
  MODIFY `itemId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=50;

--
-- AUTO_INCREMENT pre tabuľku `lists`
--
ALTER TABLE `lists`
  MODIFY `listId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT pre tabuľku `users`
--
ALTER TABLE `users`
  MODIFY `userId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=13;

--
-- Obmedzenie pre exportované tabuľky
--

--
-- Obmedzenie pre tabuľku `categories`
--
ALTER TABLE `categories`
  ADD CONSTRAINT `fk_categories_lists` FOREIGN KEY (`listId`) REFERENCES `lists` (`listId`);

--
-- Obmedzenie pre tabuľku `invitations`
--
ALTER TABLE `invitations`
  ADD CONSTRAINT `invitations_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`),
  ADD CONSTRAINT `invitations_ibfk_2` FOREIGN KEY (`listId`) REFERENCES `lists` (`listId`);

--
-- Obmedzenie pre tabuľku `items`
--
ALTER TABLE `items`
  ADD CONSTRAINT `fk_Item_Category1` FOREIGN KEY (`categoryId`) REFERENCES `categories` (`categoryId`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  ADD CONSTRAINT `fk_Item_List1` FOREIGN KEY (`listId`) REFERENCES `lists` (`listId`) ON DELETE NO ACTION ON UPDATE NO ACTION;

--
-- Obmedzenie pre tabuľku `lists`
--
ALTER TABLE `lists`
  ADD CONSTRAINT `lists_ibfk_1` FOREIGN KEY (`userId`) REFERENCES `users` (`userId`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
