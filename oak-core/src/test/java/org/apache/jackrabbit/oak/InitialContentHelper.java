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
name|plugins
operator|.
name|memory
operator|.
name|MemoryNodeStore
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
name|name
operator|.
name|NamespaceEditorProvider
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
name|nodetype
operator|.
name|TypeEditorProvider
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
name|commit
operator|.
name|CompositeEditorProvider
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
name|commit
operator|.
name|EditorHook
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
name|NodeStore
import|;
end_import

begin_comment
comment|/**  * {@code InitialContent} helper for tests  */
end_comment

begin_class
specifier|public
class|class
name|InitialContentHelper
block|{
specifier|public
specifier|static
specifier|final
name|NodeState
name|INITIAL_CONTENT
init|=
name|createInitialContent
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|NodeState
name|createInitialContent
parameter_list|()
block|{
name|NodeStore
name|store
init|=
operator|new
name|MemoryNodeStore
argument_list|()
decl_stmt|;
name|EditorHook
name|hook
init|=
operator|new
name|EditorHook
argument_list|(
operator|new
name|CompositeEditorProvider
argument_list|(
operator|new
name|NamespaceEditorProvider
argument_list|()
argument_list|,
operator|new
name|TypeEditorProvider
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|OakInitializer
operator|.
name|initialize
argument_list|(
name|store
argument_list|,
operator|new
name|InitialContent
argument_list|()
argument_list|,
name|hook
argument_list|)
expr_stmt|;
return|return
name|store
operator|.
name|getRoot
argument_list|()
return|;
block|}
specifier|private
name|InitialContentHelper
parameter_list|()
block|{}
block|}
end_class

end_unit

