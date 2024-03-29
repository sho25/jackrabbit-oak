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
name|segment
operator|.
name|CacheWeights
operator|.
name|ReaderTemplateCacheWeigher
import|;
end_import

begin_class
specifier|public
class|class
name|TemplateCache
extends|extends
name|ReaderCache
argument_list|<
name|Template
argument_list|>
block|{
comment|/**      * Create a new template cache.      *      * @param maxSize the maximum memory in bytes.      */
name|TemplateCache
parameter_list|(
name|long
name|maxSize
parameter_list|)
block|{
name|super
argument_list|(
name|maxSize
argument_list|,
literal|250
argument_list|,
literal|"Template Cache"
argument_list|,
operator|new
name|ReaderTemplateCacheWeigher
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isSmall
parameter_list|(
name|Template
name|template
parameter_list|)
block|{
name|PropertyTemplate
index|[]
name|properties
init|=
name|template
operator|.
name|getPropertyTemplates
argument_list|()
decl_stmt|;
name|PropertyState
name|mixins
init|=
name|template
operator|.
name|getMixinTypes
argument_list|()
decl_stmt|;
return|return
name|properties
operator|.
name|length
operator|==
literal|0
operator|&&
operator|(
name|mixins
operator|==
literal|null
operator|||
name|mixins
operator|.
name|count
argument_list|()
operator|==
literal|0
operator|)
return|;
block|}
block|}
end_class

end_unit

