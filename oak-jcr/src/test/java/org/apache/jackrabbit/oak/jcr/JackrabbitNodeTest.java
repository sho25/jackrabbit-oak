begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|jcr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|NodeIterator
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|Session
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|ObservationManager
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
name|JcrConstants
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
name|api
operator|.
name|JackrabbitNode
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
name|commons
operator|.
name|cnd
operator|.
name|CndImporter
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
name|test
operator|.
name|AbstractJCRTest
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
name|test
operator|.
name|api
operator|.
name|observation
operator|.
name|EventResult
import|;
end_import

begin_comment
comment|/**  * JackrabbitNodeTest: Copied and slightly adjusted from org.apache.jackrabbit.api.JackrabbitNodeTest,  * which used to create SNS for this test.  */
end_comment

begin_class
specifier|public
class|class
name|JackrabbitNodeTest
extends|extends
name|AbstractJCRTest
block|{
specifier|static
specifier|final
name|String
name|SEQ_BEFORE
init|=
literal|"abcdefghij"
decl_stmt|;
specifier|static
specifier|final
name|String
name|SEQ_AFTER
init|=
literal|"abcdefGhij"
decl_stmt|;
specifier|static
specifier|final
name|int
name|RELPOS
init|=
literal|6
decl_stmt|;
specifier|static
specifier|final
name|String
name|TEST_NODETYPES
init|=
literal|"org/apache/jackrabbit/oak/jcr/test_mixin_nodetypes.cnd"
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|testRootNode
operator|.
name|getPrimaryNodeType
argument_list|()
operator|.
name|hasOrderableChildNodes
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|char
name|c
range|:
name|SEQ_BEFORE
operator|.
name|toCharArray
argument_list|()
control|)
block|{
name|testRootNode
operator|.
name|addNode
argument_list|(
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
name|c
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|Reader
name|cnd
init|=
operator|new
name|InputStreamReader
argument_list|(
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|TEST_NODETYPES
argument_list|)
argument_list|)
decl_stmt|;
name|CndImporter
operator|.
name|registerNodeTypes
argument_list|(
name|cnd
argument_list|,
name|superuser
argument_list|)
expr_stmt|;
name|cnd
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|testRename
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Node
name|renamedNode
init|=
literal|null
decl_stmt|;
name|NodeIterator
name|it
init|=
name|testRootNode
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Node
name|n
init|=
name|it
operator|.
name|nextNode
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|n
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
name|SEQ_BEFORE
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
block|}
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|==
name|RELPOS
condition|)
block|{
name|JackrabbitNode
name|node
init|=
operator|(
name|JackrabbitNode
operator|)
name|n
decl_stmt|;
name|node
operator|.
name|rename
argument_list|(
name|name
operator|.
name|toUpperCase
argument_list|()
argument_list|)
expr_stmt|;
name|renamedNode
operator|=
name|n
expr_stmt|;
block|}
name|pos
operator|++
expr_stmt|;
block|}
name|it
operator|=
name|testRootNode
operator|.
name|getNodes
argument_list|()
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Node
name|n
init|=
name|it
operator|.
name|nextNode
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|n
operator|.
name|getName
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|new
name|String
argument_list|(
operator|new
name|char
index|[]
block|{
name|SEQ_AFTER
operator|.
name|charAt
argument_list|(
name|pos
argument_list|)
block|}
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|pos
operator|==
name|RELPOS
condition|)
block|{
name|assertTrue
argument_list|(
name|n
operator|.
name|isSame
argument_list|(
name|renamedNode
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|pos
operator|++
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testRenameEventHandling
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|Session
name|s
init|=
name|getHelper
argument_list|()
operator|.
name|getSuperuserSession
argument_list|()
decl_stmt|;
name|ObservationManager
name|mgr
init|=
name|s
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
decl_stmt|;
name|EventResult
name|result
init|=
operator|new
name|EventResult
argument_list|(
name|log
argument_list|)
decl_stmt|;
try|try
block|{
name|mgr
operator|.
name|addEventListener
argument_list|(
name|result
argument_list|,
name|Event
operator|.
name|PERSIST
operator||
name|Event
operator|.
name|NODE_ADDED
operator||
name|Event
operator|.
name|NODE_MOVED
operator||
name|Event
operator|.
name|NODE_REMOVED
argument_list|,
name|testRootNode
operator|.
name|getPath
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|NodeIterator
name|it
init|=
name|testRootNode
operator|.
name|getNodes
argument_list|()
decl_stmt|;
name|Node
name|n
init|=
name|it
operator|.
name|nextNode
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|n
operator|.
name|getName
argument_list|()
decl_stmt|;
name|JackrabbitNode
name|node
init|=
operator|(
name|JackrabbitNode
operator|)
name|n
decl_stmt|;
name|node
operator|.
name|rename
argument_list|(
name|name
operator|+
literal|'X'
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|boolean
name|foundMove
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Event
name|event
range|:
name|result
operator|.
name|getEvents
argument_list|(
literal|5000
argument_list|)
control|)
block|{
if|if
condition|(
name|Event
operator|.
name|NODE_MOVED
operator|==
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
name|foundMove
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|foundMove
condition|)
block|{
name|fail
argument_list|(
literal|"Expected NODE_MOVED event upon renaming a node."
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|mgr
operator|.
name|removeEventListener
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|s
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * @since oak 1.0      */
specifier|public
name|void
name|testSetNewMixins
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// create node with mixin test:AA
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"foo"
argument_list|,
literal|"nt:folder"
argument_list|)
decl_stmt|;
operator|(
operator|(
name|JackrabbitNode
operator|)
name|n
operator|)
operator|.
name|setMixins
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test:AA"
block|,
literal|"test:A"
block|}
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:AA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since oak 1.0      */
specifier|public
name|void
name|testSetNewMixins2
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// create node with mixin test:AA
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"foo"
argument_list|,
literal|"nt:folder"
argument_list|)
decl_stmt|;
operator|(
operator|(
name|JackrabbitNode
operator|)
name|n
operator|)
operator|.
name|setMixins
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test:A"
block|,
literal|"test:AA"
block|}
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:AA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since oak 1.0      */
specifier|public
name|void
name|testSetEmptyMixins
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// create node with mixin test:AA
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"foo"
argument_list|,
literal|"nt:folder"
argument_list|)
decl_stmt|;
name|n
operator|.
name|addMixin
argument_list|(
literal|"test:AA"
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
operator|(
operator|(
name|JackrabbitNode
operator|)
name|n
operator|)
operator|.
name|setMixins
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:AA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|n
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
argument_list|)
operator|.
name|getValues
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since oak 1.0      */
specifier|public
name|void
name|testSetRemoveMixins
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// create node with mixin test:AA
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"foo"
argument_list|,
literal|"nt:folder"
argument_list|)
decl_stmt|;
operator|(
operator|(
name|JackrabbitNode
operator|)
name|n
operator|)
operator|.
name|setMixins
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test:A"
block|,
literal|"test:AA"
block|}
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
operator|(
operator|(
name|JackrabbitNode
operator|)
name|n
operator|)
operator|.
name|setMixins
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test:A"
block|}
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:AA"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * @since oak 1.0      */
specifier|public
name|void
name|testUpdateMixins
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// create node with mixin test:AA
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"foo"
argument_list|,
literal|"nt:folder"
argument_list|)
decl_stmt|;
operator|(
operator|(
name|JackrabbitNode
operator|)
name|n
operator|)
operator|.
name|setMixins
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test:A"
block|,
literal|"test:AA"
block|}
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:AA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:A"
argument_list|)
argument_list|)
expr_stmt|;
operator|(
operator|(
name|JackrabbitNode
operator|)
name|n
operator|)
operator|.
name|setMixins
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test:A"
block|,
literal|"test:AA"
block|,
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
block|}
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:AA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
argument_list|)
expr_stmt|;
operator|(
operator|(
name|JackrabbitNode
operator|)
name|n
operator|)
operator|.
name|setMixins
argument_list|(
operator|new
name|String
index|[]
block|{
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
block|}
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:AA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
literal|"test:A"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|isNodeType
argument_list|(
name|JcrConstants
operator|.
name|MIX_REFERENCEABLE
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testSetMixins
parameter_list|()
throws|throws
name|RepositoryException
block|{
comment|// create node with mixin test:AA
name|Node
name|n
init|=
name|testRootNode
operator|.
name|addNode
argument_list|(
literal|"foo"
argument_list|,
literal|"nt:folder"
argument_list|)
decl_stmt|;
name|n
operator|.
name|addMixin
argument_list|(
literal|"test:AA"
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"test:propAA"
argument_list|,
literal|"AA"
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"test:propA"
argument_list|,
literal|"A"
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// 'downgrade' from test:AA to test:A
operator|(
operator|(
name|JackrabbitNode
operator|)
name|n
operator|)
operator|.
name|setMixins
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test:A"
block|}
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
literal|"test:propA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
literal|"test:propAA"
argument_list|)
argument_list|)
expr_stmt|;
comment|// 'upgrade' from test:A to test:AA
operator|(
operator|(
name|JackrabbitNode
operator|)
name|n
operator|)
operator|.
name|setMixins
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"test:AA"
block|}
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"test:propAA"
argument_list|,
literal|"AA"
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
literal|"test:propA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
literal|"test:propAA"
argument_list|)
argument_list|)
expr_stmt|;
comment|// replace test:AA with mix:title
operator|(
operator|(
name|JackrabbitNode
operator|)
name|n
operator|)
operator|.
name|setMixins
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"mix:title"
block|}
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:title"
argument_list|,
literal|"..."
argument_list|)
expr_stmt|;
name|n
operator|.
name|setProperty
argument_list|(
literal|"jcr:description"
argument_list|,
literal|"blah blah"
argument_list|)
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
literal|"jcr:title"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
literal|"jcr:description"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
literal|"test:propA"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|n
operator|.
name|hasProperty
argument_list|(
literal|"test:propAA"
argument_list|)
argument_list|)
expr_stmt|;
comment|// clean up
name|n
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

