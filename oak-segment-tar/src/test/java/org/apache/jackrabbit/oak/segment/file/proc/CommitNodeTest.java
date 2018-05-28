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
name|segment
operator|.
name|file
operator|.
name|proc
package|;
end_package

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

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|api
operator|.
name|Type
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
name|memory
operator|.
name|EmptyNodeState
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
name|segment
operator|.
name|file
operator|.
name|proc
operator|.
name|Proc
operator|.
name|Backend
operator|.
name|Commit
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
name|NodeState
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

begin_class
specifier|public
class|class
name|CommitNodeTest
block|{
specifier|private
name|Commit
name|mockCommit
parameter_list|()
block|{
name|Commit
name|commit
init|=
name|mock
argument_list|(
name|Commit
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|commit
operator|.
name|getRevision
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|""
argument_list|)
expr_stmt|;
return|return
name|commit
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldHaveTimestampProperty
parameter_list|()
block|{
name|Commit
name|commit
init|=
name|mockCommit
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|commit
operator|.
name|getTimestamp
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1L
argument_list|)
expr_stmt|;
name|Proc
operator|.
name|Backend
name|backend
init|=
name|mock
argument_list|(
name|Proc
operator|.
name|Backend
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|backend
operator|.
name|getCommit
argument_list|(
literal|"h"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|commit
argument_list|)
argument_list|)
expr_stmt|;
name|PropertyState
name|property
init|=
operator|new
name|CommitNode
argument_list|(
name|backend
argument_list|,
literal|"h"
argument_list|)
operator|.
name|getProperty
argument_list|(
literal|"timestamp"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|property
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1L
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldExposeRoot
parameter_list|()
block|{
name|Commit
name|commit
init|=
name|mockCommit
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|commit
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|EmptyNodeState
operator|.
name|EMPTY_NODE
argument_list|)
argument_list|)
expr_stmt|;
name|Proc
operator|.
name|Backend
name|backend
init|=
name|mock
argument_list|(
name|Proc
operator|.
name|Backend
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|backend
operator|.
name|getCommit
argument_list|(
literal|"h"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|commit
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|new
name|CommitNode
argument_list|(
name|backend
argument_list|,
literal|"h"
argument_list|)
operator|.
name|hasChildNode
argument_list|(
literal|"root"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shouldHaveRootChildNode
parameter_list|()
block|{
name|NodeState
name|root
init|=
name|EmptyNodeState
operator|.
name|EMPTY_NODE
decl_stmt|;
name|Commit
name|commit
init|=
name|mock
argument_list|(
name|Commit
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|commit
operator|.
name|getRoot
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
name|Proc
operator|.
name|Backend
name|backend
init|=
name|mock
argument_list|(
name|Proc
operator|.
name|Backend
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|backend
operator|.
name|getCommit
argument_list|(
literal|"h"
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|commit
argument_list|)
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|root
argument_list|,
operator|new
name|CommitNode
argument_list|(
name|backend
argument_list|,
literal|"h"
argument_list|)
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
