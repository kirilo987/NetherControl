### English

# NetherControl

NetherControl is a Minecraft plugin that allows server administrators to control access to the Nether. 

## Features

- **Command `/nether <open|close>`**:
  - `open`: Opens access to the Nether.
  - `close`: Closes access to the Nether and teleports all players from the Nether to their beds or the world spawn.

- **Configuration**:
  - The plugin saves the Nether access state in the `config.yml` file (`nether-open: true` or `nether-open: false`).

- **Event Handling**:
  - **PlayerPortalEvent**: Blocks player teleportation to the Nether if it is closed.
  - **PlayerRespawnEvent**: Teleports players who died in the Nether to their beds or the world spawn if the Nether is closed.

## Installation

1. Download the plugin jar file.
2. Place the jar file in the `plugins` folder of your Minecraft server.
3. Start or restart your server to load the plugin.
4. Configure the plugin by editing the `config.yml` file in the `plugins/NetherControl` directory.

## Commands

- `/nether <open|close>`: Opens or closes access to the Nether.

## Permissions

- `nethercontrol.admin`: Allows the use of the `/nether` command.

## Configuration

nether-open: true

## Author

- Kxysl1k

## Website

- [kxysl1k.netlify.app](https://kxysl1k.netlify.app/)

### Russian

# NetherControl

NetherControl - это плагин для Minecraft, который позволяет администраторам сервера контролировать доступ к аду.

## Функции

- **Команда `/nether <open|close>`**:
  - `open`: Открывает доступ к аду.
  - `close`: Закрывает доступ к аду и телепортирует всех игроков из ада к их кроватям или на мировой спавн.

- **Конфигурация**:
  - Плагин сохраняет состояние доступа к аду в файле `config.yml` (`nether-open: true` или `nether-open: false`).

- **Обработка событий**:
  - **PlayerPortalEvent**: Блокирует телепортацию игроков в ад, если он закрыт.
  - **PlayerRespawnEvent**: Телепортирует игроков, которые умерли в аду, к их кроватям или на мировой спавн, если ад закрыт.

## Установка

1. Скачайте jar-файл плагина.
2. Поместите jar-файл в папку `plugins` вашего сервера Minecraft.
3. Запустите или перезапустите сервер, чтобы загрузить плагин.
4. Настройте плагин, отредактировав файл `config.yml` в директории `plugins/NetherControl`.

## Команды

- `/nether <open|close>`: Открыть или закрыть доступ к аду.

## Права

- `nethercontrol.admin`: Позволяет использовать команду `/nether`.

## Конфигурация

```yaml
nether-open: true
```

## Автор

- Kxysl1k

## Вебсайт

- [kxysl1k.netlify.app](https://kxysl1k.netlify.app/)