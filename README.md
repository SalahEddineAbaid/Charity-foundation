# Charity-foundation

## Environment Configuration

This application requires several environment variables to be set for proper operation. These include API keys and secrets for third-party services.

### Setting Up Environment Variables

1. Copy the `env.example` file to a new file named `.env` in the root directory:
   ```
   cp env.example .env
   ```

2. Edit the `.env` file and replace the placeholder values with your actual credentials.

3. For local development, you can also create an `application-dev.properties` file:
   ```
   cp src/main/resources/application.properties src/main/resources/application-dev.properties
   ```

4. Edit `application-dev.properties` to include your local development settings.

**Important:** Never commit the `.env` file or `application-dev.properties` to version control. They are added to `.gitignore` to prevent accidental exposure of sensitive credentials.