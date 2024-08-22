# QtJambiUicTool

## Project Overview

QtJambiUicTool is a tool library that encapsulates Kotlin and QtJambi libraries. [QtJambi](https://github.com/OmixVisualization/qtjambi)It is a library that integrates the Qt framework with Java and Kotlin, allowing developers to use the powerful features of Qt in Java and Kotlin projects.Qt allows to create sophisticated user interfaces in graphical manner by using[Designer](https://doc.qt.io/qt/qtdesigner-manual.html). Designer produces a `*.ui` file containing all components and properties of the designed user interface.There are two ways to use these designed UIs in your QtJambi java application.

* ...by dynamically loading at runtime. Therefore, use the class [
  `io.qt.widgets.tools.QUiLoader`](https://doc.qt.io/qt/quiloader.html#QUiLoader) from module `qtjambi.uitools`:

``` java
QUiLoader loader = new QUiLoader();
QFile device = new QFile(":com/myapplication/widgets/mainwindow.ui");
device.open(QIODevice.OpenModeFlag.ReadOnly);
QWidget widget = loader.load(device);
device.close();
```

* ...by generating source code.
  Therefore, use the tool **UIC** available in module `qtjambi.uic`.
  Download **qtjambi-uic.jar** from the release of your choice along
  with the correponding platform-dependent **qtjambi-uic-native-X.jar** and call:

``` shell
java -Djava.library.path=<path to Qt libraries>
     -p qtjambi-6.5.8.jar:qtjambi-uic-6.5.8.jar
     -m qtjambi.uic --output=src --package=com.myapplication.widgets com/myapplication/widgets/mainwindow.ui
```

Alternative way to call it:

``` shell
java -Djava.library.path=<path to Qt libraries>
     -cp qtjambi-6.5.8.jar:qtjambi-uic-6.5.8.jar
     io.qt.uic.Main --output=src --package=com.myapplication.widgets com/myapplication/widgets/mainwindow.ui
```

**QtJambi UIC** produces the widget class in output directory (`-o`) and target package (`-p`) as java source code file.
By specifying `--generator=kotlin` you can generate Kotlin code.
The second calling method is too cumbersome,  **QtJambiUicTool** has re encapsulated it.

## Availability

This project is applicable to the following platforms:

- Windows
- macOS
- Linux

## Building QtJambiUicTool

### Preconditions

- Java 8 or 11 and higher versions
- Gradle 7.0 or higher version
- Kotlin 1.5 or higher version

### Construction steps

1. Clone the project locally:

```shell
git clone  https://gitee.com/aboutnothing/qtjambiuictool.git
cd qtjambiuictool
```

2. Build the project using Gradle:

```shell
.\gradlew shadowjar
```

### How To Use QtJambiUicTool

1. Ensure that you have built the project according to the above steps.
2. Run the generated JAR file:

```shell
java -jar build/libs/Qt-Jambi-Uic-Tool-1.0.jar 
```

### Execution Example

Assuming you have successfully built the project and generated the Qt-Jabbi-Uic-Tool-1.0.jar file. You can run the
example with the following command:

```shell
java -jar build/libs/your-project-shadow.jar -h
```

This will display help information for the QtJambiUicTool library

```bash
Usage: uic [options] [uifile]
QtJambi User Interface Compiler version 6.7.2

Options:
  -?, -h, --help                  Displays help on commandline options.
  --help-all                      Displays help, including generic Qt options.
  -v, --version                   Displays version information.
  -d, --dependencies              Display the dependencies.
  -f, --force                     Force all source files to be written.
  -s, --skip-shell                Do not generate shell class.
  -o, --output <outputDir>        Place the output into <dir>
  --in, --input <inputDir>        Place the input into <dir>
  -p, --package <package>         Place the output into <package>
  -a, --no-autoconnection         Do not generate a call to
                                  QMetaObject.connectSlotsByName().
  --postfix <postfix>             Postfix to add to all generated classnames.
  --tr, --translate <function>    Use <function> for i18n.
  -i, --imports <imports>         Add imports to comma-separated packages
                                  and/or classes.
  -g, --generator <c++|python|java|kotlin>  Select generator.
  -c, --connections <pmf|string>  Connection syntax.
  --idbased                       Use id based function for i18n

Arguments:
  [uifile]                        Input file (*.ui), otherwise stdin.
```

## Usage

```shell
#默认转换为Java代码，输出目录为当前目录
java -jar Qt-Jambi-Uic-Tool-1.0.jar test.ui
```

```shell
#-o或--output指定输出目录
java -jar Qt-Jambi-Uic-Tool-1.0.jar -o=your-outputdir test.ui
```

```shell
#-g或--generator指定生成语言为
java -jar Qt-Jambi-Uic-Tool-1.0.jar -g=kotlin test.ui
```

```shell
#-in或--input指定输入目录，将目录下所有.ui文件转换,默认输出目录为当前目录
java -jar Qt-Jambi-Uic-Tool-1.0.jar -in=your-inputdir
```

```shell
#-p或--package指定生成代码包名，最终输出目录为outptFir+package
java -jar Qt-Jambi-Uic-Tool-1.0.jar -p=org.example -o=your-outputdir test.ui
```

## Contribution

1. Fork the repository
2. Create Feat_xxx branch
3. Commit your code
4. Create Pull Request
