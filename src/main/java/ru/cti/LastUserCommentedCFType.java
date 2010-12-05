package ru.cti;

import java.util.List;
import com.atlassian.jira.issue.Issue;
import org.apache.log4j.Logger;
import com.atlassian.jira.issue.comments.Comment;
import com.atlassian.jira.issue.comments.CommentManager;
import com.atlassian.jira.issue.customfields.converters.UserConverter;
import com.atlassian.jira.issue.customfields.impl.CalculatedCFType;
import com.atlassian.jira.issue.fields.CustomField;
import com.atlassian.jira.security.JiraAuthenticationContext;
import com.opensymphony.user.User;

public class LastUserCommentedCFType extends CalculatedCFType {

	private static final Logger logger = Logger.getLogger(LastUserCommentedCFType.class);

	private final UserConverter userConverter;
	private final CommentManager commentManager;
	private final JiraAuthenticationContext authenticationContext;

	public LastUserCommentedCFType(UserConverter userConverter,
			CommentManager actionManager,
			JiraAuthenticationContext authenticationContext) {
		this.userConverter = userConverter;
		this.commentManager = actionManager;
		this.authenticationContext = authenticationContext;
	}

	public Object getSingularObjectFromString(String value) {
		return value;
	}

	public String getStringFromSingularObject(Object value) {
		assertObjectImplementsType(String.class, value);
		return value.toString();
	}

	public Object getValueFromIssue(CustomField field, Issue issue) {
		User currentUser = authenticationContext.getUser();
		String comment = null;
		try {

			List comments = commentManager.getCommentsForUser(issue,
					currentUser);
			if (comments != null && !comments.isEmpty()) {
				Comment lastComment = (Comment) comments
						.get(comments.size() - 1);
				comment= lastComment.getAuthor()+":"+lastComment.getBody();
				/*
				 * User commenter = lastComment.getUser(); if
				 * (!commenter.inGroup(JIRA_ADMIN)) { lastUser = commenter; }
				 */
			}
		} catch (Exception e) {
			logger.error("Error while get last comment", e);
		}
		return comment;
	}

}