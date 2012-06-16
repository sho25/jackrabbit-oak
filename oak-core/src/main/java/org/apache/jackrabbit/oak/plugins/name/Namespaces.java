begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/* * Licensed to the Apache Software Foundation (ASF) under one or more * contributor license agreements.  See the NOTICE file distributed with * this work for additional information regarding copyright ownership. * The ASF licenses this file to You under the Apache License, Version 2.0 * (the "License"); you may not use this file except in compliance with * the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|name
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|CoreValue
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
name|Tree
import|;
end_import

begin_comment
comment|/**  * Internal static utility class for managing the persisted namespace registry.  */
end_comment

begin_class
class|class
name|Namespaces
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|defaults
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
comment|// TODO: this should not use the "jcr" prefix
specifier|public
specifier|static
specifier|final
name|String
name|NSMAPNODENAME
init|=
literal|"jcr:namespaces"
decl_stmt|;
specifier|private
name|Namespaces
parameter_list|()
block|{     }
static|static
block|{
comment|// Standard namespace specified by JCR (default one not included)
name|defaults
operator|.
name|put
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|defaults
operator|.
name|put
argument_list|(
literal|"jcr"
argument_list|,
literal|"http://www.jcp.org/jcr/1.0"
argument_list|)
expr_stmt|;
name|defaults
operator|.
name|put
argument_list|(
literal|"nt"
argument_list|,
literal|"http://www.jcp.org/jcr/nt/1.0"
argument_list|)
expr_stmt|;
name|defaults
operator|.
name|put
argument_list|(
literal|"mix"
argument_list|,
literal|"http://www.jcp.org/jcr/mix/1.0"
argument_list|)
expr_stmt|;
name|defaults
operator|.
name|put
argument_list|(
literal|"xml"
argument_list|,
literal|"http://www.w3.org/XML/1998/namespace"
argument_list|)
expr_stmt|;
comment|// Namespace included in Jackrabbit 2.x
name|defaults
operator|.
name|put
argument_list|(
literal|"sv"
argument_list|,
literal|"http://www.jcp.org/jcr/sv/1.0"
argument_list|)
expr_stmt|;
comment|// TODO: see OAK-74
name|defaults
operator|.
name|put
argument_list|(
literal|"rep"
argument_list|,
literal|"internal"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getNamespaceMap
parameter_list|(
name|Tree
name|root
parameter_list|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|defaults
argument_list|)
decl_stmt|;
name|Tree
name|system
init|=
name|root
operator|.
name|getChild
argument_list|(
literal|"jcr:system"
argument_list|)
decl_stmt|;
if|if
condition|(
name|system
operator|!=
literal|null
condition|)
block|{
name|Tree
name|namespaces
init|=
name|system
operator|.
name|getChild
argument_list|(
name|NSMAPNODENAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|namespaces
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|PropertyState
name|property
range|:
name|namespaces
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|prefix
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|property
operator|.
name|isArray
argument_list|()
operator|&&
name|isValidPrefix
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
name|CoreValue
name|value
init|=
name|property
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|PropertyType
operator|.
name|STRING
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|value
operator|.
name|getString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
return|return
name|map
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isValidPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
comment|// TODO: Other prefix rules?
return|return
operator|!
name|prefix
operator|.
name|isEmpty
argument_list|()
operator|&&
name|prefix
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|==
operator|-
literal|1
return|;
block|}
specifier|public
specifier|static
name|boolean
name|isValidLocalName
parameter_list|(
name|String
name|local
parameter_list|)
block|{
if|if
condition|(
name|local
operator|.
name|isEmpty
argument_list|()
operator|||
literal|"."
operator|.
name|equals
argument_list|(
name|local
argument_list|)
operator|||
literal|".."
operator|.
name|equals
argument_list|(
name|local
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|local
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|ch
init|=
name|local
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"/:[]|*"
operator|.
name|indexOf
argument_list|(
name|ch
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// TODO: XMLChar check
return|return
literal|false
return|;
block|}
block|}
comment|// TODO: Other name rules?
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

