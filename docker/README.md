# Setup / Usage of Development Database

## ToC
- [Setup / Usage of Development Database](#setup--usage-of-development-database)
  - [ToC](#toc)
  - [Installation / Setup](#installation--setup)
    - [Windows](#windows)
    - [MacOS](#macos)
    - [Linux](#linux)
  - [Usage](#usage)
    - [Starting the development Containers](#starting-the-development-containers)
    - [Login](#login)
      - [Postgres](#postgres)
      - [PgAdmin4](#pgadmin4)

## Installation / Setup

### Windows
1. Download & Install [Docker Desktop](https://www.docker.com/products/docker-desktop/)
2. Make sure Docker is added to the `PATH` environment-variable
   1. Run a CMD-Instance (Press WIN+R, Enter `cmd.exe`, Press `OK`)
   2. Execute command `docker` and check if you see a big help-menu
   3. If docker is not available after installation, check [this guide](https://learn.microsoft.com/en-us/virtualization/windowscontainers/manage-docker/configure-docker-daemon)
3. Navigate a Command Prompt to this `docker`-directory
4. Start Docker Desktop
5. Run the command `docker compose up -d --build`
6. You're done :]

### MacOS
Check [this](https://docs.docker.com/desktop/setup/install/mac-install/) guide, resume from step 3 in the [Windows](#windows) guide.

### Linux
1. Install docker using your favorite/available packet manager (apt/pacman/yay/brew etc.)
2. Check if `docker compose` is available
   1. If it isn't, try installing `docker-compose` as an additional dependency
3. Resume from sep 3 in the [Windows](#windows) guide. 

## Usage

### Starting the development Containers
Depending on your installation / operation System, run `docker compose up -d --build` inside this directory.

### Login

#### Postgres
Postgres will be available at `postgres://root:toor@127.0.0.1:5432/medals`
The configuration can be checked in `develop.env` and is per default:
- Username: `root`
- Password: `root`

#### PgAdmin4
PgAdmin4 is the administration interface you can use to check / manually work with the data.
Per default, the service can be reached under the following address:
[http://127.0.0.1:4444](http://127.0.0.1)
The credentials are as follows:
- E-Mail Address: `root@postgr.es`
- Password: `toor` 

On each container restart, you'll have to add the postgres server to access it.
When you click on `Add New Server`, you'll be greeted with a modal. Enter the following values to connect to the development Postgres-Server:
1. General
   - Name: `develop`
2. Connection
   - Host name/address: `postgres-server`
   - Maintenance database: `medals`
   - Passwort: `toor`

every other property should be left as it's default value.