#!/usr/bin/env php-cgi

<!DOCTYPE html>
<html>
<body>

<?php
	$sender = $_POST['sender'];
	$version = $_POST['version'];
	$details = $_POST['description'];
	$from = 'From: ekblackm@uwaterloo.ca';
	$to = 'erinkb@gmail.com';
	$subject = 'Bug Report';
	$body = 'Reporter: $sender\n Version: $version\n Details: $details';

	if ($_POST['submit']) {
		if (mail ($to, $subject, $body, $from)) {
			echo '<p>Thank you for reporting the bug!</p>';
		} else {
			echo '<p>There was a problem submitting the report. Please try again later.</p>';
		}
	}
?>

</body>
</html>
