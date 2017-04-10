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
name|security
operator|.
name|authorization
operator|.
name|restriction
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|Value
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

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
name|ImmutableMap
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
name|Tree
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
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|Restriction
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
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionDefinition
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
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionPattern
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
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionProvider
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
name|security
operator|.
name|user
operator|.
name|AuthorizableNodeName
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
name|whiteboard
operator|.
name|DefaultWhiteboard
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
name|whiteboard
operator|.
name|Whiteboard
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|assertNull
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
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|WhiteboardRestrictionProviderTest
block|{
specifier|private
specifier|final
name|Whiteboard
name|whiteboard
init|=
operator|new
name|DefaultWhiteboard
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|WhiteboardRestrictionProvider
name|restrictionProvider
init|=
operator|new
name|WhiteboardRestrictionProvider
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Tree
name|tree
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|Tree
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
class|class
name|RestrictionException
extends|extends
name|RuntimeException
block|{}
specifier|private
name|RestrictionProvider
name|registered
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|registered
operator|=
name|Mockito
operator|.
name|mock
argument_list|(
name|RestrictionProvider
operator|.
name|class
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|registered
operator|.
name|getPattern
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|tree
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
operator|new
name|RestrictionException
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|after
parameter_list|()
block|{
name|restrictionProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testDefaultGetPattern
parameter_list|()
block|{
name|assertSame
argument_list|(
name|RestrictionPattern
operator|.
name|EMPTY
argument_list|,
name|restrictionProvider
operator|.
name|getPattern
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|tree
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testStartedGetPattern
parameter_list|()
block|{
name|restrictionProvider
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|assertSame
argument_list|(
name|RestrictionPattern
operator|.
name|EMPTY
argument_list|,
name|restrictionProvider
operator|.
name|getPattern
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|tree
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|RestrictionException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testRegisteredGetPattern
parameter_list|()
block|{
name|restrictionProvider
operator|.
name|start
argument_list|(
name|whiteboard
argument_list|)
expr_stmt|;
name|whiteboard
operator|.
name|register
argument_list|(
name|RestrictionProvider
operator|.
name|class
argument_list|,
name|registered
argument_list|,
name|ImmutableMap
operator|.
name|of
argument_list|()
argument_list|)
expr_stmt|;
name|registered
operator|.
name|getPattern
argument_list|(
name|PathUtils
operator|.
name|ROOT_PATH
argument_list|,
name|tree
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
