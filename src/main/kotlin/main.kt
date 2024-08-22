import io.qt.QtUtilities
import io.qt.core.QCommandLineOption
import io.qt.core.QCommandLineParser
import io.qt.core.QCoreApplication
import io.qt.core.QDir
import io.qt.core.QList
import io.qt.core.QScopeGuard
import io.qt.core.Qt
import io.qt.uic.Driver
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import java.util.jar.JarFile
import kotlin.system.exitProcess


private fun extractDlls(): File {
    val tempDir = File(System.getProperty("java.io.tmpdir"), "native/bin").apply {
        if (!exists()) mkdirs()
    }

    val classLoader = Thread.currentThread().contextClassLoader ?: throw IllegalStateException("ClassLoader not found")
    // 获取 JAR 文件路径
    val jarFileUrl = classLoader.getResource("bin/")?.toURI()
        ?: throw IllegalStateException("Resource 'bin/' not found in JAR")
    // 如果是 JAR 文件
    if (jarFileUrl.scheme == "jar") {
        val jarFilePath =
            jarFileUrl.rawSchemeSpecificPart.substringAfter("file:").substringBefore("!").removePrefix("/")

        // 打开 JAR 文件
        JarFile(jarFilePath).use { jarFile ->
            val entries = jarFile.entries()

            val dllFiles = mutableListOf<String>()

            // 遍历 JAR 文件中的所有条目
            while (entries.hasMoreElements()) {
                val entry = entries.nextElement()
                // 过滤出 bin 目录下的 DLL 文件
                if (entry.name.startsWith("bin/") && entry.name.endsWith(".dll")) {
                    dllFiles.add(entry.name)
                }
            }
            dllFiles.forEach { dllFileName ->
                val inputStream: InputStream = classLoader.getResourceAsStream(dllFileName)
                    ?: throw IllegalStateException("Resource '$dllFileName' not found in JAR")
                val dllFile = File(tempDir, dllFileName.substringAfter("bin/"))
                FileOutputStream(dllFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

        }
    } else {
        println("The resource 'bin/' is not inside a JAR file.")
    }
    return tempDir
}

fun main(args: Array<String>) {
    // 提取 DLL 文件
    val tempDir = extractDlls()
    // 设置 java.library.path
    System.setProperty(
        "java.library.path",
        System.getProperty("java.library.path").replace(";.", "${tempDir.absolutePath};;.")
    )

    Qt.qSetGlobalQHashSeed(0)
    QCoreApplication.setApplicationName("uic")
    QCoreApplication.setApplicationVersion(QtUtilities.qtjambiVersion().toString())
    println("currentPath: ${QDir.currentPath()}")
    QCoreApplication.initialize(args)
    QCoreApplication.setApplicationName("QtJambi UIC")
    QScopeGuard { QCoreApplication.shutdown() }.use {
        val driver = Driver()
        QCommandLineParser().apply {

            setSingleDashWordOptionMode(QCommandLineParser.SingleDashWordOptionMode.ParseAsLongOptions)
            setApplicationDescription(
                String.format(
                    "QtJambi User Interface Compiler version %1\$s",
                    QCoreApplication.applicationVersion()
                )
            )
            addHelpOption()
            addVersionOption()

            val dependenciesOption = QCommandLineOption(QList.of("d", "dependencies"), "dependencies")
            dependenciesOption.setDescription("Display the dependencies.")
            addOption(dependenciesOption)

            val forceOption = QCommandLineOption(QList.of("f", "force"), "force")
            forceOption.setDescription("Force all source files to be written.")
            addOption(forceOption)

            val skipShellOption = QCommandLineOption(QList.of("s", "skip-shell"), "skip")
            skipShellOption.setDescription("Do not generate shell class.")
            addOption(skipShellOption)

            val outputOption = QCommandLineOption(QList.of("o", "output"), "output")
            outputOption.setDescription("Place the output into <dir>")
            outputOption.setValueName("outputDir")
            addOption(outputOption)

            val inputOption = QCommandLineOption(QList.of("in", "input"), "input")
            inputOption.setDescription("Place the input into <dir>")
            inputOption.setValueName("inputDir")
            addOption(inputOption)

            val packageOption = QCommandLineOption(QList.of("p", "package"), "package")
            packageOption.setDescription("Place the output into <package>")
            packageOption.setValueName("package")
            addOption(packageOption)

            val noAutoConnectionOption = QCommandLineOption(QList.of("a", "no-autoconnection"))
            noAutoConnectionOption.setDescription("Do not generate a call to QMetaObject.connectSlotsByName().")
            addOption(noAutoConnectionOption)

            val postfixOption = QCommandLineOption("postfix")
            postfixOption.setDescription("Postfix to add to all generated classnames.")
            postfixOption.setValueName("postfix")
            addOption(postfixOption)

            val translateOption = QCommandLineOption(QList.of("tr", "translate"))
            translateOption.setDescription("Use <function> for i18n.")
            translateOption.setValueName("function")
            addOption(translateOption)

            val importOption = QCommandLineOption(QList.of("i", "imports"))
            importOption.setDescription("Add imports to comma-separated packages and/or classes.")
            importOption.setValueName("imports")
            addOption(importOption)

            val generatorOption = QCommandLineOption(QList.of("g", "generator"))
            generatorOption.setDescription("Select generator.")
            generatorOption.setValueName("c++|python|java|kotlin")
            generatorOption.setDefaultValue("java")
            generatorOption.setFlags(QCommandLineOption.Flag.HiddenFromHelp)
            addOption(generatorOption)

            val connectionsOption = QCommandLineOption(QList.of("c", "connections"))
            connectionsOption.setDescription("Connection syntax.")
            connectionsOption.setValueName("pmf|string")
            addOption(connectionsOption)

            val idBasedOption = QCommandLineOption("idbased")
            idBasedOption.setDescription("Use id based function for i18n")
            addOption(idBasedOption)

            addPositionalArgument("[uifile]", "Input file (*.ui), otherwise stdin.")

            process(QCoreApplication.arguments())

            driver.option().dependencies = isSet(dependenciesOption)
            driver.option().outputDir = QDir.fromNativeSeparators(value(outputOption))
            driver.option().targetPackage = value(packageOption).replace('/', '.')
            driver.option().autoConnection = !isSet(noAutoConnectionOption)
            driver.option().idBased = isSet(idBasedOption)
            driver.option().postfix = value(postfixOption)
            driver.option().translateFunction = value(translateOption)
            driver.option().imports = QDir.fromNativeSeparators(value(importOption))
            driver.option().forceOutput = isSet(forceOption)
            driver.option().noShellClass = isSet(skipShellOption)
            if (isSet(connectionsOption)) {
                val value = value(connectionsOption)
                when (value) {
                    "pmf" -> driver.option().forceMemberFnPtrConnectionSyntax = true
                    "string" -> driver.option().forceStringConnectionSyntax = true
                }
            }

            var language = "java"
            if (isSet(generatorOption)) {
                language = value(generatorOption).lowercase(Locale.getDefault())
                when (language) {
                    "python" -> {
                        System.err.println("QtJambi UIC could not generate python code. Use Qt's native UIC tool instead.")
                        exitProcess(-1)
                    }

                    "c++", "cpp", "cplusplus" -> {
                        System.err.println("QtJambi UIC could not generate c++ code. Use Qt's native UIC tool instead.")
                        exitProcess(-1)
                    }

                    "java", "kotlin" -> {
                        // Do nothing as these languages are supported
                    }

                    else -> {
                        System.err.println("QtJambi UIC could not generate $language code.")
                        exitProcess(-1)
                    }
                }
            }
            if (isSet(inputOption)) println("inputDir: ${value(inputOption)}")
            if (isSet(outputOption)) println("outputDir: ${value(outputOption)}")
            if (isSet(packageOption)) println("package: ${value(packageOption)}")
            if (isSet(noAutoConnectionOption)) println("noAutoConnection: true")
            if (isSet(postfixOption)) println("postfix: ${value(postfixOption)}")
            if (isSet(translateOption)) println("translateFunction: ${value(translateOption)}")
            if (isSet(importOption)) println("imports: ${value(importOption)}")
            if (isSet(forceOption)) println("forceOutput: true")
            if (isSet(skipShellOption)) println("noShellClass: true")
            if (isSet(connectionsOption)) println("connections: ${value(connectionsOption)}")
            if (isSet(idBasedOption)) println("idBased: true")
            if (isSet(generatorOption)) println("generator: ${value(generatorOption)}")
            if (isSet(inputOption) && value(inputOption).isNotEmpty()) {
                val inputDir = QDir.fromNativeSeparators(value(inputOption))
                val dir = QDir(inputDir)
                // 设置过滤器，只获取文件
                dir.setFilter(QDir.Filter.Files)
                // 设置名字过滤器，只获取 .ui 文件
                dir.setNameFilters(listOf("*.ui"))
                // 获取所有匹配的文件名
                val uiFiles = dir.entryList()
                // 输出文件路径
                if (driver.option().outputDir.isEmpty()) {
                    driver.option().outputDir = inputDir
                    println("outputDir: ${driver.option().outputDir}")
                }
                if (uiFiles.isNotEmpty())
                    uiFiles.forEach {
                        driver.uic(dir.absoluteFilePath(it), driver.option().outputDir, language)
                    }
                else
                    System.err.println("No Input Files (*.ui) Found.")
            } else {
                val inputFile = positionalArguments().firstOrNull()
                if (driver.option().dependencies) {
                    driver.printDependencies(inputFile)
                }
                inputFile?.let {
                    println("inputFile: $inputFile")
                    if (driver.option().outputDir.isEmpty()) {
                        driver.option().outputDir = inputFile.substringBeforeLast("/")
                        println("outputDir: ${driver.option().outputDir}")
                    }
                    driver.uic(it, driver.option().outputDir, language)
                }

            }
        }
    }
}