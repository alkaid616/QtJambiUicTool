# QtJambiUicTool

## 项目概述

QtJambiUicTool时一个使用 Kotlin 和 QtJambi 库封装的工具库。[QtJambi](https://github.com/OmixVisualization/qtjambi) 是一个将Qt框架与 Java 和 Kotlin 集成的库，使得开发者可以在 Java 和 Kotlin 项目中使用Qt的强大功能。Qt允许使用[Designer](https://doc.qt.io/qt/qtdesigner-manual.html)以图形方式创建复杂的用户界面。设计器生成一个`*.ui`文件,，其中包含所设计用户界面的所有组件和属性。有两种方法可以在 QtJambi 应用程序中使用这些设计的 UI。

- ...通过在运行时动态加载。因此，使用模块中的 [
  `io.qt.widgets.tools.QUiLoader`](https://doc.qt.io/qt/quiloader.html#QUiLoader) 类：`qtjambi.uitools`

```java
QUiLoader loader = new QUiLoader();
QFile device = new QFile(":com/myapplication/widgets/mainwindow.ui");
device.open(QIODevice.OpenModeFlag.ReadOnly);
        QWidget widget=loader.load(device);
        device.close();
```

- ...通过生成源代码。 因此，请使用模块中提供的 **UIC** 工具。 从您选择的版本中下载**qtjambi-uic.jar** 使用相应的平台依赖*
  *qtjambi-uic-native-X.jar**和调用：`qtjambi.uic`

```shell
java -Djava.library.path=<path to Qt libraries>
     -p qtjambi-6.5.8.jar:qtjambi-uic-6.5.8.jar
     -m qtjambi.uic --output=src --package=com.myapplication.widgets com/myapplication/widgets/mainwindow.ui
```

另一种调用方式：

```shell
java -Djava.library.path=<path to Qt libraries>
     -cp qtjambi-6.5.8.jar:qtjambi-uic-6.5.8.jar
     io.qt.uic.Main --output=src --package=com.myapplication.widgets com/myapplication/widgets/mainwindow.ui
```

**QtJambi UIC**在输出目录（）和目标包（）中生成widget类作为java源代码文件。 通过指定，您可以生成 Kotlin 代码。
`-o -p --generator=kotlin`

第二种调用方式过于繁琐，**QtJambiUicTool** 对其重新进行了封装。

## 可用性

本项目适用于以下平台：

- Windows
- macOS
- Linux

## 构建

### 前提条件

- Java 8 或 11 及更高版本
- Gradle 7.0 或更高版本
- Kotlin 1.5 或更高版本

### 构建步骤

1. 克隆项目到本地：

```shell
git clone https://gitee.com/aboutnothing/qtjambiuictool.git
cd qtjambiuictool
```

2. 使用 Gradle 构建项目：

```shell
.\gradlew shadowjar
```

### 如何使用

1. 确保你已经按照上述步骤构建了项目。
2. 运行生成的 JAR 文件：

```shell
java -jar build/libs/Qt-Jambi-Uic-Tool-1.0.jar 
```

### 执行示例

假设你已经成功构建了项目，并且生成了Qt-Jambi-Uic-Tool-1.0.jar文件。你可以通过以下命令运行示例：

```shell
java -jar build/libs/your-project-shadow.jar -h
```

这将显示 QtJambiUicTool库的帮助信息

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

## 用法

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

## 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request

