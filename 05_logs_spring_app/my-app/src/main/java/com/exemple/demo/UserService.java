package com.exemple.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Demonstration service to illustrate logging best practices
 * in a realistic business context.
 * 
 * Key points demonstrated:
 * - Using MDC (Mapped Diagnostic Context) for context
 * - Performance logs with time measurement
 * - Audit logs for traceability
 * - Business error handling
 */
@Service
public class UserService {
    
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    // Simulation of an in-memory database
    private final Map<String, User> users = new HashMap<>();
    
    /**
     * Creates a new user with complete logging
     */
    public User createUser(String name, String email) {
        // Generation of a correlation ID to trace the operation
        String correlationId = UUID.randomUUID().toString();
        
        // Adding context to MDC (Mapped Diagnostic Context)
        MDC.put("correlationId", correlationId);
        MDC.put("operation", "createUser");
        
        try {
            long startTime = System.currentTimeMillis();
            
            logger.info("🔄 Starting user creation - name={}, email={}", name, email);
            
            // Parameter validation
            if (name == null || name.trim().isEmpty()) {
                logger.warn("⚠️ Attempt to create user with empty name");
                throw new IllegalArgumentException("Name cannot be empty");
            }
            
            if (email == null || !email.contains("@")) {
                logger.warn("⚠️ Attempt to create user with invalid email: {}", email);
                throw new IllegalArgumentException("Invalid email");
            }
            
            // Email uniqueness verification
            if (users.values().stream().anyMatch(u -> u.getEmail().equals(email))) {
                logger.warn("⚠️ Attempt to create user with existing email: {}", email);
                throw new IllegalArgumentException("A user with this email already exists");
            }
            
            // Simulation d'un traitement
            simulateProcessing();
            
            // User creation
            String userId = UUID.randomUUID().toString();
            User user = new User(userId, name, email);
            users.put(userId, user);
            
            long duration = System.currentTimeMillis() - startTime;
            
            // Success log with metrics
            logger.info("✅ User created successfully - userId={}, duration={}ms", userId, duration);
            
            // Log d'audit
            logger.info("event=user_created userId={} name={} email={} timestamp={}", 
                       userId, name, email, System.currentTimeMillis());
            
            return user;
            
        } catch (Exception e) {
            logger.error("❌ Error during user creation - name={}, email={}, error={}", 
                        name, email, e.getMessage(), e);
            throw e;
        } finally {
            // MDC cleanup
            MDC.clear();
        }
    }
    
    /**
     * Retrieves a user by their ID
     */
    public User getUserById(String userId) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        MDC.put("operation", "getUserById");
        
        try {
            logger.debug("🔍 User search - userId={}", userId);
            
            if (userId == null || userId.trim().isEmpty()) {
                logger.warn("⚠️ Search attempt with empty userId");
                return null;
            }
            
            User user = users.get(userId);
            
            if (user != null) {
                logger.info("✅ User found - userId={}, name={}", userId, user.getName());
                
                // Audit log for data access
                logger.info("event=user_accessed userId={} timestamp={}", 
                           userId, System.currentTimeMillis());
            } else {
                logger.info("❌ User not found - userId={}", userId);
            }
            
            return user;
            
        } finally {
            MDC.clear();
        }
    }
    
    /**
     * Updates a user
     */
    public User updateUser(String userId, String newName, String newEmail) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        MDC.put("operation", "updateUser");
        
        try {
            long startTime = System.currentTimeMillis();
            
            logger.info("🔄 Starting user update - userId={}", userId);
            
            User existingUser = users.get(userId);
            if (existingUser == null) {
                logger.warn("⚠️ Attempt to update non-existent user - userId={}", userId);
                throw new IllegalArgumentException("User not found");
            }
            
            // Log changes for audit
            logger.info("📝 Changes detected - userId={}, oldName={}, newName={}, oldEmail={}, newEmail={}", 
                       userId, existingUser.getName(), newName, existingUser.getEmail(), newEmail);
            
            // Simulation d'un traitement
            simulateProcessing();
            
            // Update
            existingUser.setName(newName);
            existingUser.setEmail(newEmail);
            
            long duration = System.currentTimeMillis() - startTime;
            
            logger.info("✅ User updated successfully - userId={}, duration={}ms", userId, duration);
            
            // Log d'audit
            logger.info("event=user_updated userId={} name={} email={} timestamp={}", 
                       userId, newName, newEmail, System.currentTimeMillis());
            
            return existingUser;
            
        } catch (Exception e) {
            logger.error("❌ Error during user update - userId={}, error={}", 
                        userId, e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }
    
    /**
     * Deletes a user
     */
    public boolean deleteUser(String userId) {
        String correlationId = UUID.randomUUID().toString();
        MDC.put("correlationId", correlationId);
        MDC.put("operation", "deleteUser");
        
        try {
            logger.info("🗑️ Starting user deletion - userId={}", userId);
            
            User removedUser = users.remove(userId);
            
            if (removedUser != null) {
                logger.info("✅ User deleted successfully - userId={}, name={}", 
                           userId, removedUser.getName());
                
                // Critical audit log for deletion
                logger.warn("event=user_deleted userId={} name={} email={} timestamp={}", 
                           userId, removedUser.getName(), removedUser.getEmail(), System.currentTimeMillis());
                
                return true;
            } else {
                logger.info("❌ User not found for deletion - userId={}", userId);
                return false;
            }
            
        } finally {
            MDC.clear();
        }
    }
    
    /**
     * Simulates processing with possibility of error
     */
    private void simulateProcessing() {
        try {
            // Simulation of processing delay
            Thread.sleep(50 + (long)(Math.random() * 100));
            
            // Simulation of occasional error
            if (Math.random() < 0.1) { // 10% chance of error
                throw new RuntimeException("Simulated system error");
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("⚠️ Processing interrupted", e);
        }
    }
    
    /**
     * Returns the total number of users (for statistics)
     */
    public int getTotalUsers() {
        int count = users.size();
        logger.debug("📊 Total number of users: {}", count);
        return count;
    }
}
