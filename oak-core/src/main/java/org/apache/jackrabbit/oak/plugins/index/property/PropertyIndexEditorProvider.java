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
name|plugins
operator|.
name|index
operator|.
name|property
package|;
end_package

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
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_comment
comment|/**  * Service that provides PropertyIndex based editors.  *   * @see PropertyIndexEditor  * @see IndexEditorProvider  *   */
end_comment

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
specifier|public
class|class
name|PropertyIndexEditorProvider
implements|implements
name|IndexEditorProvider
block|{
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"p2"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|Editor
name|getIndexEditor
parameter_list|(
name|String
name|type
parameter_list|,
name|NodeBuilder
name|builder
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
name|PropertyIndexEditor
argument_list|(
name|builder
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

