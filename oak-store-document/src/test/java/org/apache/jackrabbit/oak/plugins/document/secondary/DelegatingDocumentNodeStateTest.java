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
name|secondary
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
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
name|plugins
operator|.
name|document
operator|.
name|AbstractDocumentNodeState
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
name|plugins
operator|.
name|document
operator|.
name|NodeStateDiffer
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
name|plugins
operator|.
name|document
operator|.
name|Revision
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
name|plugins
operator|.
name|document
operator|.
name|RevisionVector
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
name|ChildNodeEntry
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
name|EqualsDiff
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
name|secondary
operator|.
name|DelegatingDocumentNodeState
operator|.
name|PROP_LAST_REV
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
name|secondary
operator|.
name|DelegatingDocumentNodeState
operator|.
name|PROP_REVISION
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
name|assertEquals
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
name|assertSame
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
name|DelegatingDocumentNodeStateTest
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
name|basicWorking
parameter_list|()
throws|throws
name|Exception
block|{
name|RevisionVector
name|rv1
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RevisionVector
name|rv2
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|asPropertyState
argument_list|(
name|PROP_REVISION
argument_list|,
name|rv1
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|asPropertyState
argument_list|(
name|PROP_LAST_REV
argument_list|,
name|rv2
argument_list|)
argument_list|)
expr_stmt|;
name|AbstractDocumentNodeState
name|state
init|=
name|DelegatingDocumentNodeState
operator|.
name|wrap
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|NodeStateDiffer
operator|.
name|DEFAULT_DIFFER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|rv1
argument_list|,
name|state
operator|.
name|getRootRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|rv2
argument_list|,
name|state
operator|.
name|getLastRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|hasNoChildren
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|isFromExternalChange
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|metaPropertiesFilteredOut
parameter_list|()
throws|throws
name|Exception
block|{
name|setMetaProps
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|AbstractDocumentNodeState
name|state
init|=
name|DelegatingDocumentNodeState
operator|.
name|wrap
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|NodeStateDiffer
operator|.
name|DEFAULT_DIFFER
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|Iterables
operator|.
name|size
argument_list|(
name|state
operator|.
name|getProperties
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|state
operator|.
name|getPropertyCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|childNodeDecorated
parameter_list|()
throws|throws
name|Exception
block|{
name|setMetaProps
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|setMetaProps
argument_list|(
name|builder
operator|.
name|child
argument_list|(
literal|"a"
argument_list|)
argument_list|)
expr_stmt|;
name|setMetaProps
argument_list|(
name|builder
operator|.
name|child
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|AbstractDocumentNodeState
name|state
init|=
name|DelegatingDocumentNodeState
operator|.
name|wrap
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|NodeStateDiffer
operator|.
name|DEFAULT_DIFFER
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"a"
argument_list|)
operator|instanceof
name|AbstractDocumentNodeState
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"b"
argument_list|)
operator|instanceof
name|AbstractDocumentNodeState
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|hasChildNode
argument_list|(
literal|"c"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"c"
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|state
operator|.
name|hasNoChildren
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|ChildNodeEntry
name|cne
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|assertTrue
argument_list|(
name|cne
operator|.
name|getNodeState
argument_list|()
operator|instanceof
name|AbstractDocumentNodeState
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|state
operator|.
name|getChildNodeCount
argument_list|(
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|withRootRevision
parameter_list|()
throws|throws
name|Exception
block|{
name|RevisionVector
name|rv1
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|RevisionVector
name|rv2
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|3
argument_list|)
argument_list|)
decl_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|asPropertyState
argument_list|(
name|PROP_REVISION
argument_list|,
name|rv1
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|asPropertyState
argument_list|(
name|PROP_LAST_REV
argument_list|,
name|rv2
argument_list|)
argument_list|)
expr_stmt|;
name|AbstractDocumentNodeState
name|state
init|=
name|DelegatingDocumentNodeState
operator|.
name|wrap
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|NodeStateDiffer
operator|.
name|DEFAULT_DIFFER
argument_list|)
decl_stmt|;
name|AbstractDocumentNodeState
name|state2
init|=
name|state
operator|.
name|withRootRevision
argument_list|(
name|rv1
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|assertSame
argument_list|(
name|state
argument_list|,
name|state2
argument_list|)
expr_stmt|;
name|RevisionVector
name|rv4
init|=
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|4
argument_list|)
argument_list|)
decl_stmt|;
name|AbstractDocumentNodeState
name|state3
init|=
name|state
operator|.
name|withRootRevision
argument_list|(
name|rv4
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|rv4
argument_list|,
name|state3
operator|.
name|getRootRevision
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|state3
operator|.
name|isFromExternalChange
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|wrapIfPossible
parameter_list|()
throws|throws
name|Exception
block|{
name|assertFalse
argument_list|(
name|DelegatingDocumentNodeState
operator|.
name|wrapIfPossible
argument_list|(
name|EMPTY_NODE
argument_list|,
name|NodeStateDiffer
operator|.
name|DEFAULT_DIFFER
argument_list|)
operator|instanceof
name|AbstractDocumentNodeState
argument_list|)
expr_stmt|;
name|setMetaProps
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DelegatingDocumentNodeState
operator|.
name|wrapIfPossible
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|NodeStateDiffer
operator|.
name|DEFAULT_DIFFER
argument_list|)
operator|instanceof
name|AbstractDocumentNodeState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|equals1
parameter_list|()
throws|throws
name|Exception
block|{
name|setMetaProps
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|NodeBuilder
name|b2
init|=
name|EMPTY_NODE
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|setProperty
argument_list|(
literal|"foo"
argument_list|,
literal|"bar"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|DelegatingDocumentNodeState
operator|.
name|wrap
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|NodeStateDiffer
operator|.
name|DEFAULT_DIFFER
argument_list|)
argument_list|,
name|b2
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|EqualsDiff
operator|.
name|equals
argument_list|(
name|b2
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|DelegatingDocumentNodeState
operator|.
name|wrap
argument_list|(
name|builder
operator|.
name|getNodeState
argument_list|()
argument_list|,
name|NodeStateDiffer
operator|.
name|DEFAULT_DIFFER
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|setMetaProps
parameter_list|(
name|NodeBuilder
name|nb
parameter_list|)
block|{
name|nb
operator|.
name|setProperty
argument_list|(
name|asPropertyState
argument_list|(
name|PROP_REVISION
argument_list|,
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|nb
operator|.
name|setProperty
argument_list|(
name|asPropertyState
argument_list|(
name|PROP_LAST_REV
argument_list|,
operator|new
name|RevisionVector
argument_list|(
operator|new
name|Revision
argument_list|(
literal|1
argument_list|,
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|PropertyState
name|asPropertyState
parameter_list|(
name|String
name|name
parameter_list|,
name|RevisionVector
name|revision
parameter_list|)
block|{
return|return
name|createProperty
argument_list|(
name|name
argument_list|,
name|revision
operator|.
name|asString
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit
