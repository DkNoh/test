import os
import re
import shutil

BASE_DIR = r"c:\Work\sms-project-v2\src\main\java\com\example\sms"
CONTROLLER_DIR = os.path.join(BASE_DIR, "controller")
SERVICE_DIR = os.path.join(BASE_DIR, "service")

# Mapping of class names to domain packages
MAPPING = {
    # Controllers
    "AccountController": "account",
    "LoginController": "account",
    "UserApiController": "account",
    
    "ApprovalController": "approval",
    
    "AuthorityController": "system",
    "CommonCodeApiController": "system",
    "MenuManageController": "system",
    "SystemController": "system",
    "SystemEmployeeController": "system",
    "SystemScaffoldController": "system",
    
    "CampaignController": "sms",
    "DeptStatController": "sms",
    "SmsHistoryController": "sms",
    "StatisticsController": "sms",
    "campaignSearchController": "sms",
    
    "HistoryTestController": "history",
    
    "BasicController": "common",
    "FileApiController": "common",
    "IndexController": "common",
    
    # Services
    "ApprovalService": "approval",
    
    "AuditLogService": "system",
    "AuthorityService": "system",
    "CommonCodeService": "system",
    "EmployeeService": "system",
    "MenuManageService": "system",
    "MenuService": "system",
    
    "DeptStatService": "sms",
    "SmsHistoryService": "sms",
    "campaignSearchService": "sms",
    
    "HistoryTestService": "history",
    
    "FileService": "common",
}

def ensure_dir(path):
    if not os.path.exists(path):
        os.makedirs(path)

# Step 1: Move files and update their package declarations
old_to_new_imports = {}

def process_directory(directory, layer_name):
    if not os.path.exists(directory): return
    for filename in os.listdir(directory):
        if not filename.endswith(".java"): continue
        
        filepath = os.path.join(directory, filename)
        if not os.path.isfile(filepath): continue
        
        class_name = filename[:-5] # remove .java
        if class_name in MAPPING:
            domain = MAPPING[class_name]
            target_dir = os.path.join(directory, domain)
            ensure_dir(target_dir)
            target_path = os.path.join(target_dir, filename)
            
            # Read content
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            # Update package
            old_package = f"package com.example.sms.{layer_name};"
            new_package = f"package com.example.sms.{layer_name}.{domain};"
            content = content.replace(old_package, new_package)
            
            # Write to new path
            with open(target_path, 'w', encoding='utf-8') as f:
                f.write(content)
                
            # Remove old file
            os.remove(filepath)
            
            # Record for import replacements
            old_import = f"com.example.sms.{layer_name}.{class_name}"
            new_import = f"com.example.sms.{layer_name}.{domain}.{class_name}"
            old_to_new_imports[old_import] = new_import

print("Moving files and updating package declarations...")
process_directory(CONTROLLER_DIR, "controller")
process_directory(SERVICE_DIR, "service")

# Step 2: Scan all .java files and update imports
print("Updating import statements across the project...")
for root, dirs, files in os.walk(BASE_DIR):
    for filename in files:
        if filename.endswith(".java"):
            filepath = os.path.join(root, filename)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            modified = False
            for old_imp, new_imp in old_to_new_imports.items():
                if old_imp in content:
                    # Replace fully qualified imports
                    content = content.replace("import " + old_imp + ";", "import " + new_imp + ";")
                    # Also replace fully qualified names in annotations or generic code if they appear without 'import'
                    content = content.replace(old_imp + ".", new_imp + ".")
                    modified = True
            
            if modified:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(content)

print("Refactoring completed successfully!")
