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
name|index
operator|.
name|counter
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|CommitFailedException
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
name|index
operator|.
name|IndexEditorProvider
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
name|index
operator|.
name|IndexUpdateCallback
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
name|index
operator|.
name|counter
operator|.
name|jmx
operator|.
name|NodeCounter
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
name|Editor
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
name|mount
operator|.
name|MountInfoProvider
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
name|mount
operator|.
name|Mounts
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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Component
import|;
end_import

begin_import
import|import
name|org
operator|.
name|osgi
operator|.
name|service
operator|.
name|component
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_class
annotation|@
name|Component
argument_list|(
name|service
operator|=
name|IndexEditorProvider
operator|.
name|class
argument_list|)
specifier|public
class|class
name|NodeCounterEditorProvider
implements|implements
name|IndexEditorProvider
block|{
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"counter"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|RESOLUTION
init|=
literal|"resolution"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SEED
init|=
literal|"seed"
decl_stmt|;
annotation|@
name|Reference
specifier|private
name|MountInfoProvider
name|mountInfoProvider
init|=
name|Mounts
operator|.
name|defaultMountInfoProvider
argument_list|()
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Editor
name|getIndexEditor
parameter_list|(
annotation|@
name|NotNull
name|String
name|type
parameter_list|,
annotation|@
name|NotNull
name|NodeBuilder
name|definition
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|root
parameter_list|,
annotation|@
name|NotNull
name|IndexUpdateCallback
name|callback
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|TYPE
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|resolution
decl_stmt|;
name|PropertyState
name|s
init|=
name|definition
operator|.
name|getProperty
argument_list|(
name|RESOLUTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|resolution
operator|=
name|NodeCounterEditor
operator|.
name|DEFAULT_RESOLUTION
expr_stmt|;
block|}
else|else
block|{
name|resolution
operator|=
name|s
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
name|long
name|seed
decl_stmt|;
name|s
operator|=
name|definition
operator|.
name|getProperty
argument_list|(
name|SEED
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|seed
operator|=
name|s
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|seed
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|NodeCounter
operator|.
name|COUNT_HASH
condition|)
block|{
comment|// create a random number (that way we can also check if this feature is enabled)
name|seed
operator|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|getMostSignificantBits
argument_list|()
expr_stmt|;
name|definition
operator|.
name|setProperty
argument_list|(
name|SEED
argument_list|,
name|seed
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|NodeCounter
operator|.
name|USE_OLD_COUNTER
condition|)
block|{
name|NodeCounterEditorOld
operator|.
name|NodeCounterRoot
name|rootData
init|=
operator|new
name|NodeCounterEditorOld
operator|.
name|NodeCounterRoot
argument_list|(
name|resolution
argument_list|,
name|seed
argument_list|,
name|definition
argument_list|,
name|root
argument_list|,
name|callback
argument_list|)
decl_stmt|;
return|return
operator|new
name|NodeCounterEditorOld
argument_list|(
name|rootData
argument_list|,
literal|null
argument_list|,
literal|"/"
argument_list|,
literal|null
argument_list|)
return|;
block|}
else|else
block|{
name|NodeCounterEditor
operator|.
name|NodeCounterRoot
name|rootData
init|=
operator|new
name|NodeCounterEditor
operator|.
name|NodeCounterRoot
argument_list|(
name|resolution
argument_list|,
name|seed
argument_list|,
name|definition
argument_list|,
name|root
argument_list|,
name|callback
argument_list|)
decl_stmt|;
return|return
operator|new
name|NodeCounterEditor
argument_list|(
name|rootData
argument_list|,
name|mountInfoProvider
argument_list|)
return|;
block|}
block|}
specifier|public
name|NodeCounterEditorProvider
name|with
parameter_list|(
name|MountInfoProvider
name|mountInfoProvider
parameter_list|)
block|{
name|this
operator|.
name|mountInfoProvider
operator|=
name|mountInfoProvider
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

