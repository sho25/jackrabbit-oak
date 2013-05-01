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
import|import static
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
operator|.
name|STRING
import|;
end_import

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
name|NamespaceRegistry
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
name|JcrConstants
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
specifier|public
class|class
name|Namespaces
implements|implements
name|NamespaceConstants
block|{
specifier|private
name|Namespaces
parameter_list|()
block|{     }
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|DEFAULTS
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
static|static
block|{
comment|// Standard namespace specified by JCR (default one not included)
name|DEFAULTS
operator|.
name|put
argument_list|(
name|NamespaceRegistry
operator|.
name|PREFIX_EMPTY
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_EMPTY
argument_list|)
expr_stmt|;
name|DEFAULTS
operator|.
name|put
argument_list|(
name|NamespaceRegistry
operator|.
name|PREFIX_JCR
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_JCR
argument_list|)
expr_stmt|;
name|DEFAULTS
operator|.
name|put
argument_list|(
name|NamespaceRegistry
operator|.
name|PREFIX_NT
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_NT
argument_list|)
expr_stmt|;
name|DEFAULTS
operator|.
name|put
argument_list|(
name|NamespaceRegistry
operator|.
name|PREFIX_MIX
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_MIX
argument_list|)
expr_stmt|;
name|DEFAULTS
operator|.
name|put
argument_list|(
name|NamespaceRegistry
operator|.
name|PREFIX_XML
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_XML
argument_list|)
expr_stmt|;
comment|// Namespace included in Jackrabbit 2.x
name|DEFAULTS
operator|.
name|put
argument_list|(
name|PREFIX_SV
argument_list|,
name|NAMESPACE_SV
argument_list|)
expr_stmt|;
name|DEFAULTS
operator|.
name|put
argument_list|(
name|PREFIX_REP
argument_list|,
name|NAMESPACE_REP
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
name|DEFAULTS
argument_list|)
decl_stmt|;
name|Tree
name|namespaces
init|=
name|root
operator|.
name|getChild
argument_list|(
name|JcrConstants
operator|.
name|JCR_SYSTEM
argument_list|)
operator|.
name|getChild
argument_list|(
name|REP_NAMESPACES
argument_list|)
decl_stmt|;
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
name|String
name|value
init|=
name|property
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|STRING
operator|.
name|equals
argument_list|(
name|property
operator|.
name|getType
argument_list|()
argument_list|)
condition|)
block|{
name|map
operator|.
name|put
argument_list|(
name|prefix
argument_list|,
name|value
argument_list|)
expr_stmt|;
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

