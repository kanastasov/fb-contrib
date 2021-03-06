/*
 * fb-contrib - Auxiliary detectors for Java programs
 * Copyright (C) 2005-2016 Dave Brosius
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.mebigfatguy.fbcontrib.detect;

import java.util.Set;

import com.mebigfatguy.fbcontrib.utils.BugType;
import com.mebigfatguy.fbcontrib.utils.FQMethod;
import com.mebigfatguy.fbcontrib.utils.UnmodifiableSet;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.BytecodeScanningDetector;

/**
 * looks for method calls to collection classes where the method is not defined by the Collections interface, and an equivalent method exists in the interface.
 */
public class NonCollectionMethodUse extends BytecodeScanningDetector {
    private static final Set<FQMethod> oldMethods = UnmodifiableSet.create(new FQMethod("java/util/Hashtable", "contains", "(java/lang/Object)Z"),
            new FQMethod("java/util/Hashtable", "elements", "()Ljava/util/Enumeration;"),
            new FQMethod("java/util/Hashtable", "keys", "()Ljava/util/Enumeration;"), new FQMethod("java/util/Vector", "addElement", "(Ljava/lang/Object;)V"),
            new FQMethod("java/util/Vector", "elementAt", "(I)Ljava/lang/Object;"),
            new FQMethod("java/util/Vector", "insertElementAt", "(Ljava/lang/Object;I)V"), new FQMethod("java/util/Vector", "removeAllElements", "()V"),
            new FQMethod("java/util/Vector", "removeElement", "(Ljava/lang/Object;)Z"), new FQMethod("java/util/Vector", "removeElementAt", "(I)V"),
            new FQMethod("java/util/Vector", "setElementAt", "(Ljava/lang/Object;I)V"));

    private BugReporter bugReporter;

    /**
     * constructs a NCMU detector given the reporter to report bugs on
     *
     * @param bugReporter
     *            the sync of bug reports
     */
    public NonCollectionMethodUse(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    /**
     * implements the visitor to look for method calls that are one of the old pre-collections1.2 set of methods
     *
     * @param seen
     *            the currently parsed opcode
     */
    @Override
    public void sawOpcode(int seen) {
        if (seen == INVOKEVIRTUAL) {
            String className = getClassConstantOperand();
            String methodName = getNameConstantOperand();
            String methodSig = getSigConstantOperand();

            FQMethod methodInfo = new FQMethod(className, methodName, methodSig);
            if (oldMethods.contains(methodInfo)) {
                bugReporter.reportBug(new BugInstance(this, BugType.NCMU_NON_COLLECTION_METHOD_USE.name(), NORMAL_PRIORITY).addClass(this).addMethod(this)
                        .addSourceLine(this));
            }
        }
    }
}
