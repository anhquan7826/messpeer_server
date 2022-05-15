-- phpMyAdmin SQL Dump
-- version 5.1.3
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: May 15, 2022 at 01:12 PM
-- Server version: 10.4.24-MariaDB
-- PHP Version: 7.4.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `messpear`
--

-- --------------------------------------------------------

--
-- Table structure for table `group_name`
--

CREATE TABLE `group_name` (
  `group_id` varchar(50) COLLATE utf32_vietnamese_ci NOT NULL,
  `group_name` varchar(50) COLLATE utf32_vietnamese_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf32 COLLATE=utf32_vietnamese_ci;

--
-- Dumping data for table `group_name`
--

INSERT INTO `group_name` (`group_id`, `group_name`) VALUES
('765GYVyvjjh78Hgjhgu', 'Group 2'),
('dBHygjhV675G87G76', 'Group 1');

-- --------------------------------------------------------

--
-- Table structure for table `group_user`
--

CREATE TABLE `group_user` (
  `group_id` varchar(50) COLLATE utf32_vietnamese_ci NOT NULL,
  `username` varchar(50) COLLATE utf32_vietnamese_ci NOT NULL,
  `is_host` char(1) COLLATE utf32_vietnamese_ci NOT NULL DEFAULT 'X'
) ENGINE=InnoDB DEFAULT CHARSET=utf32 COLLATE=utf32_vietnamese_ci;

--
-- Dumping data for table `group_user`
--

INSERT INTO `group_user` (`group_id`, `username`, `is_host`) VALUES
('765GYVyvjjh78Hgjhgu', 'anhquan7826', 'X'),
('dBHygjhV675G87G76', 'anhquan7826', 'X');

-- --------------------------------------------------------

--
-- Table structure for table `message`
--

CREATE TABLE `message` (
  `message_id` varchar(50) COLLATE utf32_vietnamese_ci NOT NULL,
  `user_id` varchar(50) COLLATE utf32_vietnamese_ci NOT NULL,
  `group_id` varchar(50) COLLATE utf32_vietnamese_ci NOT NULL,
  `timestamp` datetime(5) NOT NULL,
  `content` text COLLATE utf32_vietnamese_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf32 COLLATE=utf32_vietnamese_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `username` varchar(50) COLLATE utf32_vietnamese_ci NOT NULL,
  `email` varchar(500) COLLATE utf32_vietnamese_ci NOT NULL,
  `password_hash` bigint(11) NOT NULL,
  `first_name` varchar(50) COLLATE utf32_vietnamese_ci NOT NULL,
  `last_name` varchar(50) COLLATE utf32_vietnamese_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf32 COLLATE=utf32_vietnamese_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`username`, `email`, `password_hash`, `first_name`, `last_name`) VALUES
('anhquan7826', 'anhquan7826@gmail.com', 2032556195, 'Nguyen Anh', 'Quan');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `group_name`
--
ALTER TABLE `group_name`
  ADD PRIMARY KEY (`group_id`);

--
-- Indexes for table `group_user`
--
ALTER TABLE `group_user`
  ADD PRIMARY KEY (`group_id`,`username`),
  ADD KEY `username` (`username`);

--
-- Indexes for table `message`
--
ALTER TABLE `message`
  ADD PRIMARY KEY (`message_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `message_ibfk_1` (`group_id`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`username`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `group_user`
--
ALTER TABLE `group_user`
  ADD CONSTRAINT `group_user_ibfk_1` FOREIGN KEY (`username`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `group_user_ibfk_2` FOREIGN KEY (`group_id`) REFERENCES `group_name` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `message`
--
ALTER TABLE `message`
  ADD CONSTRAINT `message_ibfk_1` FOREIGN KEY (`group_id`) REFERENCES `group_name` (`group_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `message_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`username`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
