package com.exemple.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {
    
    // SLF4J Logger - best practice: one logger per class
    private static final Logger logger = LoggerFactory.getLogger(DemoApplication.class);
    
    @Autowired
    private UserService userService;
    
    public static void main(String[] args) {
        logger.info("🚀 Starting the log demonstration application");
        
        try {
            SpringApplication.run(DemoApplication.class, args);
            logger.info("✅ Application started successfully");
        } catch (Exception e) {
            logger.error("❌ Error during application startup", e);
            System.exit(1);
        }
    }
    
    /**
     * Bean that runs at startup to demonstrate different types of logs
     */
    @Bean
    public CommandLineRunner demonstrationLogs() {
        return args -> {
            logger.info("=== LOG LEVELS DEMONSTRATION ===");
            
            // 1. TRACE - Very detailed information (usually disabled in production)
            logger.trace("🔍 TRACE: Very detailed debugging information");
            
            // 2. DEBUG - Debugging information
            logger.debug("🐛 DEBUG: Useful information for debugging");
            
            // 3. INFO - General information about operation
            logger.info("ℹ️ INFO: Application is running normally");
            
            // 4. WARN - Warnings (potentially problematic situations)
            logger.warn("⚠️ WARN: Unusual situation detected, but application continues");
            
            // Demonstration of logs with context
            demonstrateLogsWithContext();
            
            // Demonstration of error handling
            demonstrateErrorHandling();
            
            // Demonstration of structured logs
            demonstrateStructuredLogs();
            
            // Demonstration of business service with contextual logs
            demonstrateBusinessService();
            
            logger.info("=== END OF DEMONSTRATION ===");
        };
    }
    
    /**
     * Demonstrates the use of logs with contextual information
     */
    private void demonstrateLogsWithContext() {
        logger.info("--- Logs with context ---");
        
        String userId = "user123";
        String operation = "login";
        long duration = 150;
        
        // Log with contextual information - useful for monitoring
        logger.info("Operation {} completed for user {} in {}ms", 
                   operation, userId, duration);
        
        // Log with more context
        logger.info("Order processing - userId={}, orderId={}, amount={}, status={}",
                   "user456", "order789", 99.99, "COMPLETED");
    }
    
    /**
     * Demonstrates error handling with appropriate logs
     */
    private void demonstrateErrorHandling() {
        logger.info("--- Error handling ---");
        
        try {
            // Simulation of an operation that can fail
            simulateRiskyOperation();
        } catch (IllegalArgumentException e) {
            // Error log with stack trace
            logger.error("❌ Error during risky operation: {}", e.getMessage(), e);
        }
        
        // Example of warning log for a recoverable situation
        try {
            simulateOperationWithFallback();
        } catch (Exception e) {
            logger.warn("⚠️ Main operation failed, using fallback: {}", e.getMessage());
            logger.info("✅ Fallback executed successfully");
        }
    }
    
    /**
     * Demonstrates the use of structured logs (key=value format)
     */
    private void demonstrateStructuredLogs() {
        logger.info("--- Structured logs ---");
        
        // Structured logs - facilitates automated analysis
        logger.info("event=user_registration userId=user789 email=user@example.com source=web timestamp={}",
                   System.currentTimeMillis());
        
        logger.info("event=api_call endpoint=/api/users method=GET responseTime=45ms status=200");
        
        logger.info("event=database_query table=users operation=SELECT duration=12ms rowCount=5");
    }
    
    /**
     * Simulates an operation that can throw an exception
     */
    private void simulateRiskyOperation() {
        logger.debug("🔄 Executing risky operation...");
        
        // Simulation of an error condition
        if (Math.random() > 0.5) {
            throw new IllegalArgumentException("Invalid parameter detected");
        }
        
        logger.info("✅ Risky operation completed successfully");
    }
    
    /**
     * Simulates an operation with fallback mechanism
     */
    private void simulateOperationWithFallback() {
        logger.debug("🔄 Attempting main operation...");
        
        // Simulation of a failure
        throw new RuntimeException("External service unavailable");
    }
    
    /**
     * Demonstrates the use of logs in a realistic business context
     */
    private void demonstrateBusinessService() {
        logger.info("--- Business service demonstration ---");
        
        try {
            // Creating users
            User user1 = userService.createUser("Alice Smith", "alice@example.com");
            User user2 = userService.createUser("Bob Johnson", "bob@example.com");
            
            // User search
            User foundUser = userService.getUserById(user1.getId());
            
            // Update
            userService.updateUser(user1.getId(), "Alice Brown", "alice.brown@example.com");
            
            // Attempt to create with duplicate email (generates a warning)
            try {
                userService.createUser("Charlie Wilson", "alice.brown@example.com");
            } catch (IllegalArgumentException e) {
                logger.info("✅ Functional validation: {}", e.getMessage());
            }
            
            // Statistics
            int totalUsers = userService.getTotalUsers();
            logger.info("📊 Total number of users created: {}", totalUsers);
            
            // Deletion (critical audit log)
            userService.deleteUser(user2.getId());
            
        } catch (Exception e) {
            logger.error("❌ Error in business service demonstration", e);
        }
    }
}
