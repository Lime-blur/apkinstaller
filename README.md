# apkinstaller
[![Решение проблем](https://img.shields.io/badge/Решение%20проблем-welcome-brightgreen.svg?style=flat)](https://github.com/Lime-blur/apkinstaller/issues)

Приложение для установки apk файлов на Android устройство.

### Цель приложения
Обновление приложения из apk файла с помощью Intent и PackageInstaller с перезапуском приложения после обновления.

### Возможности приложния
- Обновление приложения из apk файла с помощью Intent с Mime Type "application/vnd.android.package-archive"
  - Из внутреннего хранилища приложения
  - С помощью выбора файла в файловом менеджере
  - После скачивания файла во внутреннее хранилище приложения
 - Обновление приложения из apk файла с помощью [InstallStart.java](https://android.googlesource.com/platform/packages/apps/PackageInstaller/+/ab39f6cb7afc48584da3c59d8e2a5e1ef121aafb/src/com/android/packageinstaller/InstallStart.java)
 - Обновление приложения из apk файла с помощью PackageInstaller
   - Из внутреннего хранилища приложения

### Проблемы с PackageInstaller и анализ
- Для запуска диалога завершения установки необходимо пройти полный путь установки
  - **Описание проблемы:** параметр "exported" имеет значение "false" во всех Activities, кроме InstallStart, UninstallerActivity в [AndroidManifest.xml](https://android.googlesource.com/platform/packages/apps/PackageInstaller/+/ab39f6cb7afc48584da3c59d8e2a5e1ef121aafb/AndroidManifest.xml)
  - **Решение проблемы:** -
- Запуск Activity из BroadcastReceiver после обновления приложения не даёт никаких результатов (даже при использовании различных флагов, процессов, taskAffinity, нескольких Activity в одном приложении и т.п.)
  - **Описание проблемы:** после обновления приложения, Activities приложения становятся фоновыми и система запрещает их запуск
  - **Решение проблемы:** проблема решается, если перед запуском необходимой Activity, запустить Activity настроек устройства
- Создание двух приложений, где одно приложение - инициатор обновления, другое приложение - установщик обновления
  - **Описание проблемы:** при установке и удалении приложения, необходимо проходить процесс установки и удаления для каждого приложения отдельно
  - **Решение проблемы:** -
