begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|reference
package|;
end_package

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
name|index
operator|.
name|reference
operator|.
name|NodeReferenceConstants
operator|.
name|TYPE
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
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
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
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Property
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Reference
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|scr
operator|.
name|annotations
operator|.
name|Service
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
name|IndexConstants
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

begin_class
annotation|@
name|Component
annotation|@
name|Service
argument_list|(
name|IndexEditorProvider
operator|.
name|class
argument_list|)
annotation|@
name|Property
argument_list|(
name|name
operator|=
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
argument_list|,
name|value
operator|=
name|NodeReferenceConstants
operator|.
name|TYPE
argument_list|,
name|propertyPrivate
operator|=
literal|true
argument_list|)
specifier|public
class|class
name|ReferenceEditorProvider
implements|implements
name|IndexEditorProvider
block|{
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
specifier|public
name|Editor
name|getIndexEditor
parameter_list|(
annotation|@
name|Nonnull
name|String
name|type
parameter_list|,
annotation|@
name|Nonnull
name|NodeBuilder
name|definition
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|root
parameter_list|,
annotation|@
name|Nonnull
name|IndexUpdateCallback
name|callback
parameter_list|)
block|{
if|if
condition|(
name|TYPE
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
operator|new
name|ReferenceEditor
argument_list|(
name|definition
argument_list|,
name|root
argument_list|,
name|mountInfoProvider
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|ReferenceEditorProvider
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

