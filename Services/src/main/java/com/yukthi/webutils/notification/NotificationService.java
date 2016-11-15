package com.yukthi.webutils.notification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yukthi.persistence.ITransaction;
import com.yukthi.persistence.TransactionException;
import com.yukthi.persistence.repository.RepositoryFactory;
import com.yukthi.utils.exceptions.InvalidStateException;
import com.yukthi.webutils.common.models.notification.NotificationModel;
import com.yukthi.webutils.common.models.notification.NotificationSetting;
import com.yukthi.webutils.repository.UserEntity;

/**
 * Notification service to manage notifications and user customizations.
 * 
 * @author akiran
 */
@Service
public class NotificationService
{
	private static Logger logger = LogManager.getLogger(NotificationService.class);
	
	/**
	 * Repository factory to fetch repository.
	 */
	@Autowired
	private RepositoryFactory repositoryFactory;
	
	/**
	 * User notification repository.
	 */
	private IUserNotificationRepository userNotificationRepository;
	
	/**
	 * Map maintaining notification types.
	 */
	private Map<String, NotificationType> nameToType = new HashMap<>();
	
	/**
	 * Post construct method to initialize repository.
	 */
	@PostConstruct
	private void init()
	{
		userNotificationRepository = repositoryFactory.getRepository(IUserNotificationRepository.class);
	}
	
	/**
	 * Registers specified notification type.
	 * @param notificationType Type to register.
	 */
	public void registerNotficationType(NotificationType notificationType)
	{
		nameToType.put(notificationType.getName(), notificationType);
	}
	
	/**
	 * Sets the specified notification types for specified user.
	 * @param userId User for which notifications has to be set.
	 * @param notifications Notifications to be set.
	 */
	public void setUserPrefereneces(long userId, List<NotificationSetting> notifications)
	{
		try(ITransaction transaction = userNotificationRepository.newOrExistingTransaction())
		{
			UserEntity userEntity = new UserEntity(userId);
			NotificationType notificationType = null;
			
			//add new preferences
			for(NotificationSetting setting : notifications)
			{
				notificationType = nameToType.get(setting.getNotificationType());
				
				if(!notificationType.isOptional())
				{
					continue;
				}
				
				if(!userNotificationRepository.updateUserPreference(setting.getNotificationType(), userId, setting.isEnabled()))
				{
					if(!userNotificationRepository.save( new UserNotificationEntity(userEntity, setting.getNotificationType(), setting.isEnabled()) ))
					{
						throw new InvalidStateException("Failed to set notification setting for type '{}' for user with id - {}", setting.getNotificationType(), userId);
					}
				}
			}
		}catch(TransactionException ex)
		{
			logger.error("An error occurred while setting user preferences for notification", ex);
			throw new InvalidStateException(ex, "An error occurred while setting user preferences");
		}
	}
	
	/**
	 * Fetches user preferences for notifications filling gaps for missing notifications with default enablement.
	 * @param userId User id for which settings needs to be fetched.
	 * @return Matching notification settings.
	 */
	public List<NotificationModel> getUserPreferences(long userId)
	{
		List<NotificationModel> settings = userNotificationRepository.fetchNotificationsForUser(userId);
		
		if(settings == null)
		{
			settings = Collections.emptyList();
		}
		
		Map<String, NotificationModel> typeToModel = settings.stream()
			.collect(Collectors.<NotificationModel, String, NotificationModel>toMap(model -> model.getNotificationType(), model -> model));
		
		NotificationType notificationType = null;
		NotificationModel notificationModel = null;
		
		for(String type : nameToType.keySet())
		{
			notificationType = nameToType.get(type);
			notificationModel = typeToModel.get(type);
			
			if(notificationModel == null)
			{
				typeToModel.put( type, new NotificationModel(type, notificationType.isDefaultEnabled(), notificationType.isOptional(), notificationType.getDescription()) );
			}
			else
			{
				notificationModel.setDescritpion(notificationType.getDescription());
				notificationModel.setOptional(notificationType.isOptional());
			}
		}
		
		return new ArrayList<>(typeToModel.values());
	}
	
	/**
	 * Filters users for specified notification type.
	 * @param notificationTypeName Type for which filtering needs to be done.
	 * @param userIds User ids for which notification can be sent.
	 * @return Filtered user ids based on user preferences.
	 */
	public Set<Long> filterUsersForNotification(String notificationTypeName, Collection<Long> userIds)
	{
		List<NotificationSettingResult> filteredUsers = userNotificationRepository.fetchFilterUsers(notificationTypeName, userIds);
		
		//if no user customizations found in db for this notification type 
		if(filteredUsers == null || filteredUsers.isEmpty())
		{
			filteredUsers = Collections.emptyList();
		}
		
		Map<Long, NotificationSettingResult> resMap = filteredUsers.stream().collect(
				Collectors.<NotificationSettingResult, Long, NotificationSettingResult>toMap(setting -> setting.getUserId(), setting -> setting));
		
		Set<Long> filteredUserIds = new HashSet<>();
		NotificationType notificationType = nameToType.get(notificationTypeName);
		
		for(Long userId : userIds)
		{
			//if user does not have setting for this notification
			if(!resMap.containsKey(userId))
			{
				if(notificationType.isDefaultEnabled() || !notificationType.isOptional())
				{
					filteredUserIds.add(userId);
				}
				
				continue;
			}
			
			if(resMap.get(userId).isEnabled())
			{
				filteredUserIds.add(userId);
			}
		}
		
		return filteredUserIds;
	}
}
