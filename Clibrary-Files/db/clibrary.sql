-- phpMyAdmin SQL Dump
-- version 4.5.1
-- http://www.phpmyadmin.net
--
-- Host: 127.0.0.1
-- Generation Time: Jan 02, 2017 at 12:35 PM
-- Server version: 10.1.13-MariaDB
-- PHP Version: 7.0.6

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `clibrary`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `ID` int(11) NOT NULL,
  `Name` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`ID`, `Name`) VALUES
(1, 'my category'),
(2, 'test'),
(3, 'my cat'),
(-1, 'default'),
(4, 'test cat');

-- --------------------------------------------------------

--
-- Table structure for table `friends`
--

CREATE TABLE `friends` (
  `ID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  `FriendID` int(11) NOT NULL,
  `Accepted` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `friends`
--

INSERT INTO `friends` (`ID`, `UserID`, `FriendID`, `Accepted`) VALUES
(1, 1, 2, 1),
(2, 2, 1, 1),
(4, 58, 2, 1),
(5, 2, 58, 1),
(8, 3, 2, 1),
(7, 2, 3, 1),
(9, 3, 80, 0),
(10, 3, 1, 1),
(18, 79, 3, 1),
(22, 88, 3, 1),
(21, 3, 88, 1);

-- --------------------------------------------------------

--
-- Table structure for table `groups`
--

CREATE TABLE `groups` (
  `ID` int(11) NOT NULL,
  `Name` text NOT NULL,
  `AdminID` int(11) NOT NULL,
  `usersIDs` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `groups`
--

INSERT INTO `groups` (`ID`, `Name`, `AdminID`, `usersIDs`) VALUES
(1, 'test', 1, '2,1'),
(2, 'tss', 2, '1,2');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `ID` int(11) NOT NULL,
  `Name` text NOT NULL,
  `Email` text NOT NULL,
  `Password` text NOT NULL,
  `Image` text NOT NULL,
  `Profession` text NOT NULL,
  `status` int(11) NOT NULL,
  `FBuserid` varchar(500) NOT NULL,
  `location` varchar(500) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`ID`, `Name`, `Email`, `Password`, `Image`, `Profession`, `status`, `FBuserid`, `location`) VALUES
(80, 'ahmad', 'ahmad@g.com', '123123', 'noimage.png', '', 1, '', ''),
(81, 'biss', 'biss@biss.com', '123123', 'noimage.png', '', 1, '', ''),
(79, 'ahmad', 'abouhmed.ma@gmail.com', '123123', 'noimage.png', '', 1, '', ''),
(78, 'ahmad', 'abouhmed.ma@gmail.com', '123123', 'noimage.png', '', 1, '', ''),
(77, 'ahmad', 'abouhmed.ma@gmail.com', '123123', 'noimage.png', '', 1, '', ''),
(76, 't', 'tt@tt.com', '123123', 'noimage.png', '', 1, '', ''),
(75, 't', 'ssa@ssa.com', '123123', 'noimage.png', '', 1, '', ''),
(73, 'tt', 'ts@ts.com', '123123', 'noimage.png', '', 1, '', ''),
(74, 'tt', 'tsa@tsa.com', '123123', 'noimage.png', '', 1, '', ''),
(72, 'tt', 'tt@tt.com', '123123', 'noimage.png', '', 1, '', ''),
(70, 'ss', 'ss@ss.com', '123123', 'noimage.png', '', 1, '', ''),
(71, 'tt', 'tt@tt.com', '123123', 'noimage.png', '', 1, '', ''),
(69, 'ss', 's@s.com', '123123', 'noimage.png', '', 1, '', ''),
(68, 'sis', 'sis@sis.com', '123123', 'noimage.png', '', 1, '', ''),
(66, 'ss', 'ss@ss.com', '123123', 'noimage.png', '', 1, '', ''),
(67, 'sis', 'ss@ss.com', '123123', 'noimage.png', '', 1, '', ''),
(65, 't', 'tt@tt.com', '123123', 'noimage.png', ' ', 1, '', ''),
(64, 't', 'tt@tt.com', '123123', 'noimage.png', ' ', 1, '', ''),
(62, 't', 'tt@tt.com', '123123', 'noimage.png', '', 1, '', ''),
(63, 't', 'tt@tt.com', '123123', 'noimage.png', ' ', 1, '', ''),
(61, 'tsc', 'tsc@tsc.com', '123123', 'noimage.png', '', 1, '', ''),
(58, 'biss', 't@t.com', '123123', 'noimage.png', '', 1, '', ''),
(59, 'c', 'cc@cc.com', '123123', 'noimage.png', '', 1, '', ''),
(2, 'c', 'c@c.com', '123123', 'image-78796C05-2E43-4948-A271-DEE1D31E4806.png', '', 1, '', ''),
(60, 'c', 'cct@cc.com', '123123', 'noimage.png', '', 1, '', ''),
(1, 'Bassem A. Alameddine', '', '1iv9vil7b1e92', 'image-D55CBDFC-50EF-45E8-8D5F-A299B08DC8CC.png', 'biss', 1, '10207401480860773', ''),
(82, 'biss', 'biss@biss.com', '123123', 'noimage.png', '', 1, '', ''),
(83, 'tst', 'tst@tst.com', '123123', 'noimage.png', '', 1, '', ''),
(84, 'biss', 'biss@biss.com', '123123', 'noimage.png', '', 1, '', ''),
(85, 'ts', 'ts@ts.com', '123123', 'noimage.png', '', 1, '', ''),
(3, 'Biss Alam', 'chirahama@gmail.com', '1h7wh28p8qvxz', 'image-29586A37-CCD4-4429-9C7A-164A24A7BF03.png', '', 1, '', ''),
(88, 'ahmad maassarani', 'ahmadmaassarani@gmail.com', '1jiak63nf1x6b', 'image-D78AB67A-427F-4976-9151-A6A11232C7E1.png', '', 1, '', '');

-- --------------------------------------------------------

--
-- Table structure for table `usersnotifications`
--

CREATE TABLE `usersnotifications` (
  `ID` int(11) NOT NULL,
  `userID` int(11) NOT NULL,
  `token` text NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `usersnotifications`
--

INSERT INTO `usersnotifications` (`ID`, `userID`, `token`) VALUES
(123, 88, ''),
(124, 3, 'd9DDdZK9iTw:APA91bFbU4jfIt-HzLEA80gEeJ5Q69X0Fn4jRV1YUna7lhj64zxhYRHt2SRx3hOe91y9mm_tG4a4JKtnQouqsYPBosRg_Tu1YTjnSV8a-L3lkq88rWwV-HO4VuDyYcfdqOe_cXoccImQ');

-- --------------------------------------------------------

--
-- Table structure for table `user_categories`
--

CREATE TABLE `user_categories` (
  `ID` int(11) NOT NULL,
  `CategoryID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_categories`
--

INSERT INTO `user_categories` (`ID`, `CategoryID`, `UserID`) VALUES
(1, 1, 1),
(2, 2, 57),
(3, 3, 2),
(4, -1, 73),
(5, -1, 74),
(6, -1, 75),
(12, 4, 80),
(8, -1, 76),
(20, -1, 3),
(19, -1, 87),
(14, -1, 84),
(21, -1, 88),
(16, -1, 85);

-- --------------------------------------------------------

--
-- Table structure for table `user_files`
--

CREATE TABLE `user_files` (
  `ID` int(11) NOT NULL,
  `UserID` int(11) NOT NULL,
  `Name` text NOT NULL,
  `GroupID` int(11) NOT NULL,
  `FriendID` int(11) NOT NULL,
  `IsDownloaded` int(11) NOT NULL,
  `CategoryID` int(11) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_files`
--

INSERT INTO `user_files` (`ID`, `UserID`, `Name`, `GroupID`, `FriendID`, `IsDownloaded`, `CategoryID`) VALUES
(1, 57, 'Rncp2 rapport version r3-FINAL.docx', 0, 53, 0, -1),
(2, 57, 'Rncp2 rapport.docx', 0, 53, 0, -1),
(3, 57, 'Rncp2 rapport.docx', 0, 53, 0, -1),
(4, 57, 'Rncp2 rapport.docx', 0, 53, 0, -1),
(5, 57, 'Rncp2 rapport.docx', -1, 53, 0, 2),
(6, 1, 'Rncp2 rapport.docx', 2, -1, 0, 1),
(7, 58, 'Rncp2 rapport.docx', 0, 2, 0, -1),
(8, 58, 'Rncp2 rapport version r3-FINAL.docx', 0, 2, 0, -1),
(9, 3, 'Rncp2 rapport.docx', 0, 1, 0, -1),
(10, 3, 'Rncp2 rapport.docx', 0, 1, 0, -1),
(11, 3, 'Rncp2 rapport version r3-FINAL.docx', 0, 1, 0, -1),
(12, 3, 'Rncp2 rapport.docx', 0, 1, 0, -1),
(13, 79, 'rmsINFO.txt', 0, 3, 1, -1),
(14, 3, 'Rncp2 rapport.docx', 0, 88, 1, -1),
(15, 88, 'miniclipId.txt', 0, 3, 1, -1),
(16, 88, 'rmsINFO.txt', 0, 3, 1, -1),
(17, 88, 'miniclipId.txt', 0, 3, 1, -1),
(18, 3, 'Rncp2 rapport.docx', 0, 88, 0, -1);

-- --------------------------------------------------------

--
-- Table structure for table `user_requestes`
--

CREATE TABLE `user_requestes` (
  `ID` int(11) NOT NULL,
  `userID` int(11) NOT NULL,
  `RequestedUserID` int(11) NOT NULL,
  `Accepted` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `user_requestes`
--

INSERT INTO `user_requestes` (`ID`, `userID`, `RequestedUserID`, `Accepted`) VALUES
(2, 1, 3, 1),
(3, 3, 79, 1),
(6, 88, 3, 1);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `friends`
--
ALTER TABLE `friends`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `groups`
--
ALTER TABLE `groups`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `usersnotifications`
--
ALTER TABLE `usersnotifications`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `user_categories`
--
ALTER TABLE `user_categories`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `user_files`
--
ALTER TABLE `user_files`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `user_requestes`
--
ALTER TABLE `user_requestes`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;
--
-- AUTO_INCREMENT for table `friends`
--
ALTER TABLE `friends`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=23;
--
-- AUTO_INCREMENT for table `groups`
--
ALTER TABLE `groups`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=89;
--
-- AUTO_INCREMENT for table `usersnotifications`
--
ALTER TABLE `usersnotifications`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=125;
--
-- AUTO_INCREMENT for table `user_categories`
--
ALTER TABLE `user_categories`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;
--
-- AUTO_INCREMENT for table `user_files`
--
ALTER TABLE `user_files`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;
--
-- AUTO_INCREMENT for table `user_requestes`
--
ALTER TABLE `user_requestes`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
