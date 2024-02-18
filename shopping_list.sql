-- phpMyAdmin SQL Dump
-- version 5.2.0
-- https://www.phpmyadmin.net/
--
-- Hostiteľ: 127.0.0.1
-- Čas generovania: Sun 18.Feb 2024, 18:00
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
  `parentCategoryId` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Sťahujem dáta pre tabuľku `categories`
--

INSERT INTO `categories` (`categoryId`, `name`, `parentCategoryId`) VALUES
(1, 'category', NULL),
(3, 'category2', NULL);

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
(1, 5, NULL, 'xxx', 1, 0, NULL, NULL),
(2, 5, NULL, 'bbbb', 1, 0, NULL, NULL),
(3, 5, 1, 'aaa', 1, 0, NULL, NULL),
(4, 5, 3, 'dsafdasf', 0, 0, NULL, NULL),
(5, 5, 1, 'aaa', 1, 0, NULL, NULL),
(6, 5, 3, 'dsafdasf', 1, 0, NULL, NULL);

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
(5, 3, 'xb', '2024-02-17 18:12:50', '2024-02-17 18:12:50', 'x');

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
(3, 'mato.turek@gmail.com', 'mato', '$2y$10$RlLgoUji/5xCpfKZ.dkWg.65gLVQ500w7yYIXXJRqhjWyBy81WQjC'),
(5, 'mato.turek2@gmail.com', 'mato', '$2y$10$TgkCwI5LkB0f3JtxPy5K0uQBsR3qBzMwD.l2LAM4e4OHk7HKBu9RS'),
(6, 'mato.turek3@gmail.com', 'mato', '$2y$10$orNFK2aqcx5OgDglfc4/i.64tMrQ65wSxsI7MKg1mwBHBbmMxWfmy'),
(7, 'mato.turek43@gmail.com', 'mato', '$2y$10$.S8NMB8qw78mXm9kRPnjKefbn9atEditcKXYnqcLzQRc8bewIrXpm'),
(8, 'mato.turek5@gmail.com', 'mato', '$2y$10$7q7BmEq//6CXv31/RyeH5uIcPtZbexy0PVi5MHuNsTI7DHjV9aJY6');

--
-- Kľúče pre exportované tabuľky
--

--
-- Indexy pre tabuľku `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`categoryId`),
  ADD KEY `parentCategoryId` (`parentCategoryId`);

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
  MODIFY `categoryId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT pre tabuľku `items`
--
ALTER TABLE `items`
  MODIFY `itemId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT pre tabuľku `lists`
--
ALTER TABLE `lists`
  MODIFY `listId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT pre tabuľku `users`
--
ALTER TABLE `users`
  MODIFY `userId` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Obmedzenie pre exportované tabuľky
--

--
-- Obmedzenie pre tabuľku `categories`
--
ALTER TABLE `categories`
  ADD CONSTRAINT `categories_ibfk_1` FOREIGN KEY (`parentCategoryId`) REFERENCES `categories` (`categoryId`);

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
