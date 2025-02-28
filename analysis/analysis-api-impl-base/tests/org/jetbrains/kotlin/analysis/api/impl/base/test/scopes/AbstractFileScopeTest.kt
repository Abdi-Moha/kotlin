/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.impl.base.test.scopes

import org.jetbrains.kotlin.analysis.api.impl.barebone.test.FrontendApiTestConfiguratorService
import org.jetbrains.kotlin.analysis.api.impl.base.test.test.framework.AbstractHLApiSingleFileTest
import org.jetbrains.kotlin.analysis.api.symbols.DebugSymbolRenderer
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.test.model.TestModule
import org.jetbrains.kotlin.test.services.TestServices
import org.jetbrains.kotlin.test.services.assertions

abstract class AbstractFileScopeTest : AbstractHLApiSingleFileTest() {
    override fun doTestByFileStructure(ktFile: KtFile, module: TestModule, testServices: TestServices) {
        val actual = executeOnPooledThreadInReadAction {
            analyseForTest(ktFile) {
                val symbol = ktFile.getFileSymbol()
                val scope = symbol.getFileScope()
                with(DebugSymbolRenderer) {
                    val renderedSymbol = renderExtra(symbol)
                    val callableNames = scope.getPossibleCallableNames()
                    val renderedCallables = scope.getCallableSymbols().map { renderExtra(it) }
                    val classifierNames = scope.getPossibleClassifierNames()
                    val renderedClassifiers = scope.getClassifierSymbols().map { renderExtra(it) }

                    "FILE SYMBOL:\n" + renderedSymbol + "\n" +
                            "\nCALLABLE NAMES:\n" + callableNames.joinToString(prefix = "[", postfix = "]\n", separator = ", ") +
                            "\nCALLABLE SYMBOLS:\n" + renderedCallables.joinToString(separator = "\n\n", postfix = "\n") +
                            "\nCLASSIFIER NAMES:\n" + classifierNames.joinToString(prefix = "[", postfix = "]\n", separator = ", ") +
                            "\nCLASSIFIER SYMBOLS:\n" + renderedClassifiers.joinToString(separator = "\n\n")
                }
            }
        }

        testServices.assertions.assertEqualsToTestDataFileSibling(actual)
    }
}