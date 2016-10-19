begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|bundlor
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|PropertyState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|asList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Type
operator|.
name|STRINGS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|document
operator|.
name|bundlor
operator|.
name|DocumentBundlor
operator|.
name|PROP_PATTERN
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|memory
operator|.
name|PropertyStates
operator|.
name|createProperty
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|DocumentBundlorTest
block|{
specifier|private
name|NodeBuilder
name|builder
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|basicSetup
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|PROP_PATTERN
argument_list|,
name|asList
argument_list|(
literal|"x"
argument_list|,
literal|"x/y"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|DocumentBundlor
name|bundlor
init|=
name|DocumentBundlor
operator|.
name|from
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bundlor
operator|.
name|isBundled
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bundlor
operator|.
name|isBundled
argument_list|(
literal|"x/y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bundlor
operator|.
name|isBundled
argument_list|(
literal|"x/y/z"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bundlor
operator|.
name|isBundled
argument_list|(
literal|"z"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|invalid
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentBundlor
operator|.
name|from
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|asPropertyState
parameter_list|()
throws|throws
name|Exception
block|{
name|builder
operator|.
name|setProperty
argument_list|(
name|createProperty
argument_list|(
name|PROP_PATTERN
argument_list|,
name|asList
argument_list|(
literal|"x"
argument_list|,
literal|"x/y"
argument_list|,
literal|"z"
argument_list|)
argument_list|,
name|STRINGS
argument_list|)
argument_list|)
expr_stmt|;
name|DocumentBundlor
name|bundlor
init|=
name|DocumentBundlor
operator|.
name|from
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|)
decl_stmt|;
name|PropertyState
name|ps
init|=
name|bundlor
operator|.
name|asPropertyState
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|ps
argument_list|)
expr_stmt|;
name|DocumentBundlor
name|bundlor2
init|=
name|DocumentBundlor
operator|.
name|from
argument_list|(
name|ps
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|bundlor2
operator|.
name|isBundled
argument_list|(
literal|"x"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|bundlor2
operator|.
name|isBundled
argument_list|(
literal|"x/y"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|bundlor2
operator|.
name|isBundled
argument_list|(
literal|"m"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

