# Declaring properties

By default, plugin picks the properties from the `bytemask.properties` file.
If you've configured the different name
from the [configuration](Configure.md "Configure custom file name and class name"), then you'll have to create a file
with that name.

Once configuration is done, Build the project 🔨 and class will be generated.

## Example

### Global configuration

Create `.properties` file in `/app` directory

```Generic
API_KEY=AI984013oindh48
SERVER_SECRET=SUPERSECRET!!!
```

### Variant specific configuration

Create `.properties` file in variant source directory:

- `/app/src/debug`

```Generic
API_KEY=DEBUG-AI984013oindh48
SERVER_SECRET=SUPERSECRET!!!DEBUG
```

- `/app/src/release`

```Generic
API_KEY=PROD-AI984013oindh48
SERVER_SECRET=SUPERSECRET!!!PROD
```

### Flavour specific configuration

Create `.properties` file in flavour source directory:

Assuming we have `free` and `paid` variants, example:

- `/app/src/freeDebug`

```Generic
API_KEY=DEBUG-AI984013oindh48
SERVER_SECRET=SUPERSECRET!!!DEBUG
ADS_API_KEY=exaih8YJBhbhjBHJBhj
```

- `/app/src/paidRelease`

```Generic
API_KEY=PROD-AI984013oindh48
SERVER_SECRET=SUPERSECRET!!!PROD
PAYMENT_API_KEY=uguUGYGyg8G&IUG
```

> Properties will always be picked in the following order:
> 1. Flavour
> 2. Variant
> 3. Global