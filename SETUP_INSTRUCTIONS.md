# Setup Instructions for Splitwise Project

## Maven Installation

### For Windows:

1. **Download Maven:**
   - Go to https://maven.apache.org/download.cgi
   - Download the latest `apache-maven-3.9.x-bin.zip` file

2. **Extract Maven:**
   - Extract to a location like `C:\Program Files\Apache\maven` or `C:\maven`

3. **Add Maven to PATH:**
   - Open System Properties → Environment Variables
   - Under "System Variables", find and select "Path", then click "Edit"
   - Click "New" and add: `C:\Program Files\Apache\maven\bin` (or wherever you extracted Maven)
   - Click "OK" to save

4. **Verify Installation:**
   - Open a new PowerShell/Command Prompt window
   - Run: `mvn --version`
   - You should see Maven version information

### Alternative: Use Maven Wrapper (No Installation Needed)

If you prefer not to install Maven globally, we can set up Maven Wrapper which includes Maven in the project.

## Running the Project

### Option 1: Using Command Line (after Maven is installed)
```bash
cd splitwise-discovery
mvn clean compile
mvn spring-boot:run
```

### Option 2: Using Eclipse IDE
1. Right-click on `splitwise-discovery` project
2. Select "Run As" → "Maven build..."
3. Enter goals: `spring-boot:run`
4. Click "Run"

### Option 3: Using Eclipse Run Configuration
1. Right-click on `DiscoveryServiceApplication.java`
2. Select "Run As" → "Java Application"

## Prerequisites Checklist

- [ ] Java 17 or higher installed (`java -version`)
- [ ] Maven installed and in PATH (`mvn --version`)
- [ ] MySQL 8.0+ installed (for later services)
- [ ] Eclipse IDE with M2E (Maven for Eclipse) plugin

