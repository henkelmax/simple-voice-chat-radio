# Simple Radio

A server-side Simple Voice Chat addon mod for Fabric adding a radio block capable of streaming mp3 radio streams.

## Usage

- Install this mod alongside [Simple Voice Chat](https://modrinth.com/plugin/simple-voice-chat) on your server
- Make sure you set up the voice chat mod correctly (see [here](https://modrepo.de/minecraft/voicechat/wiki/server_setup))
- Get the mp3 livestream URL of your favorite radio station
- Enter the command `/radio create "<stream-url" "<station-name>"` to create a radio block
- Place the radio block
- Right-click the radio block to turn it on

## Commands

- `/radio create "<stream-url" "<station-name>"` - Gives you a radio playing the provided mp3 stream

## Configuration

| Property                   | Description                                            | Default |
|----------------------------|--------------------------------------------------------|---------|
| `radio_range`              | The audible range of radios                            | `48.0`  |
| `command_permission_level` | The permission level required to use the radio command | `0`     |
| `radio_skin_url`           | The skin url for the radio block                       |         |
| `show_music_particles`     | Whether to show music particles                        | `true`  |
| `music_particle_frequency` | The frequency of the music particles in milliseconds   | `2000`  |

## Credits

- [Radio Skin](https://minecraft-heads.com/custom-heads/decoration/215-radio)