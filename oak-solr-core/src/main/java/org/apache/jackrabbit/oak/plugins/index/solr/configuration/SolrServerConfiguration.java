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
name|solr
operator|.
name|configuration
package|;
end_package

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|ParameterizedType
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
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
name|solr
operator|.
name|server
operator|.
name|SolrServerProvider
import|;
end_import

begin_comment
comment|/**  * Configuration parameters for starting a {@link org.apache.solr.client.solrj.SolrClient}  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|SolrServerConfiguration
parameter_list|<
name|S
extends|extends
name|SolrServerProvider
parameter_list|>
block|{
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
specifier|private
specifier|volatile
name|Constructor
argument_list|<
name|?
argument_list|>
name|constructor
decl_stmt|;
specifier|protected
name|SolrServerConfiguration
parameter_list|()
block|{
name|Type
name|superclass
init|=
name|getClass
argument_list|()
operator|.
name|getGenericSuperclass
argument_list|()
decl_stmt|;
if|if
condition|(
name|superclass
operator|instanceof
name|Class
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Missing type parameter."
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
operator|(
operator|(
name|ParameterizedType
operator|)
name|superclass
operator|)
operator|.
name|getActualTypeArguments
argument_list|()
index|[
literal|0
index|]
expr_stmt|;
block|}
specifier|public
name|Type
name|getType
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
specifier|public
name|S
name|getProvider
parameter_list|()
throws|throws
name|IllegalAccessException
throws|,
name|InvocationTargetException
throws|,
name|InstantiationException
block|{
if|if
condition|(
name|constructor
operator|==
literal|null
condition|)
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|rawType
init|=
name|type
operator|instanceof
name|Class
argument_list|<
name|?
argument_list|>
condition|?
operator|(
name|Class
argument_list|<
name|?
argument_list|>
operator|)
name|type
else|:
call|(
name|Class
argument_list|<
name|?
argument_list|>
call|)
argument_list|(
operator|(
name|ParameterizedType
operator|)
name|type
argument_list|)
operator|.
name|getRawType
argument_list|()
decl_stmt|;
name|Constructor
argument_list|<
name|?
argument_list|>
index|[]
name|constructors
init|=
name|rawType
operator|.
name|getConstructors
argument_list|()
decl_stmt|;
for|for
control|(
name|Constructor
argument_list|<
name|?
argument_list|>
name|c
range|:
name|constructors
control|)
block|{
if|if
condition|(
name|c
operator|.
name|getParameterTypes
argument_list|()
operator|.
name|length
operator|==
literal|1
operator|&&
name|c
operator|.
name|getParameterTypes
argument_list|()
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|constructor
operator|=
name|c
expr_stmt|;
block|}
block|}
if|if
condition|(
name|constructor
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InstantiationException
argument_list|(
literal|"missing constructor SolrServerProvider(SolrServerConfiguration) for type "
operator|+
name|rawType
argument_list|)
throw|;
block|}
block|}
return|return
operator|(
name|S
operator|)
name|constructor
operator|.
name|newInstance
argument_list|(
name|this
argument_list|)
return|;
comment|// TODO : each SolrServerProvider impl. is forced to have a constructor with a SolrServerConfiguration, fix?
block|}
block|}
end_class

end_unit

