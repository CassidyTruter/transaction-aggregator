# Cassidy's Transaction Aggregation System

## Notes to my examiner(s)
Dear Examiner,

I'll spare you an AI-written essay. I just need to point out a few things
about my project.

Firstly, I've tried to make the project easy to run, so I pushed the .env files
and the keystore.
Please know that I would never do this in a real-world scenario!

Secondly, I ran out of time to implement some important things that would be 
necessary in a production-ready system. The main ones I want to highlight are: 
- **In-memory authentication**: I know this is impractical for a real system, since
one would be limited to the one user I've accounted for (admin). This makes scaling 
very problematic. And on top of that, it poses security risks because there are
various ways that the credentials can be exposed, since they are in plain text in the 
.env file. I was intending to store user credentials in a database with password
hashing. 
- **Basic Auth**: I only had time to implement Basic Auth for authentication
(with TLS for prod), but if I had more time, I would have liked to implement OAuth2
authentication for prod. If I had done that, I would have used dev to show that my 
implementation works, and prod would have been the OAuth2 version which would not
have been directly testable because I wouldn't have had access to a real OAuth2 provider.
- **Testing**: You will see that my testing is currently a script that needs to be manually 
checked. I hope that this script will be helpful to you as an examiner, but I realise that 
if I wanted my system to be production-ready, I would have needed automated tests.

Hopefully, by explaining how these components are not production-ready, you will at least
know that I am aware that they are problematic. I hope you don't find too many issues 
that I have totally overlooked. 

Lastly, the way that I have mocked my data sources is designed to mimic a 
Kafka-like producer-consumer architecture, where the `TransactionQueue` acts as an in-memory 
message broker. I did that because I figured that Kafka topics would most likely be used 
in a real-world transaction-aggregation system. As a result, you'll see that the number of 
transactions in the database will increase every 10 seconds. 


## How to build, run and test this project

### Prod vs Dev
There are two environments that can be run. I would recommend running dev, so I'm giving those 
instructions first, but I'll give the prod instructions afterwards. The main difference is that prod
uses TLS.

### Prerequisites
- Clone repository
- Install Docker and Docker Compose
- Install curl for testing

### Commands for Dev

```bash
# Build and start
docker-compose -f docker-compose.dev.yml build --no-cache
docker-compose -f docker-compose.dev.yml --env-file .env.dev up -d
```

**Access:** `http://localhost:8080/swagger-ui.html`

**Credentials:** `admin` / `devPassword123`

**Testing:**
```bash
# Run test script
chmod +x test-api.sh
./test-api.sh
```

Examples of curl commands to test manually
```bash
# Get summary by category
curl -u admin:devPassword123 http://localhost:8080/api/transactions/summary
# Filter by category
curl -u admin:devPassword123 "http://localhost:8080/api/transactions?category=INCOME"
# Filter by account
curl -u admin:devPassword123 "http://localhost:8080/api/transactions?accountNumber=ACC-12345-001"
# Test health endpoint
curl http://localhost:8080/actuator/health
# Test filter by categories
curl -u admin:devPassword123 "http://localhost:8080/api/transactions?category=SHOPPING&subcategory=GROCERIES"
```

**Stopping and cleaning up (deletes persisted volumes):**
```bash
docker-compose -f docker-compose.dev.yml down -v
```

### Commands for Prod

```bash
# Build and start
docker-compose -f docker-compose.prod.yml build --no-cache
docker-compose -f docker-compose.prod.yml --env-file .env up -d
```

**Credentials:** `prodAdmin` / `kiBkwb84th9Kj2Bm0`

**Testing:**
```bash
# Run test script
chmod +x test-api.sh
./test-api.sh prod
```

Examples of curl commands to test manually
```bash
# Get summary by category
curl -k -u prodAdmin:kiBkwb84th9Kj2Bm0 https://localhost:8443/api/transactions/summary

# Filter by category
curl -k -u prodAdmin:kiBkwb84th9Kj2Bm0 "https://localhost:8443/api/transactions?category=INCOME"

# Filter by account
curl -k -u prodAdmin:kiBkwb84th9Kj2Bm0 "https://localhost:8443/api/transactions?accountNumber=ACC-12345-001"

# Test health endpoint
curl -k https://localhost:8443/actuator/health

# Test filter by categories
curl -k -u prodAdmin:kiBkwb84th9Kj2Bm0 "https://localhost:8443/api/transactions?category=SHOPPING&subcategory=GROCERIES"
```

**Stopping and cleaning up (deletes persisted volumes):**
```bash
docker-compose -f docker-compose.prod.yml down -v
```