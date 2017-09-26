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
name|spi
operator|.
name|namespace
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
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

begin_comment
comment|/**  * TODO document  */
end_comment

begin_interface
specifier|public
interface|interface
name|NamespaceConstants
block|{
name|String
name|REP_NAMESPACES
init|=
literal|"rep:namespaces"
decl_stmt|;
name|String
name|NAMESPACES_PATH
init|=
literal|'/'
operator|+
name|JcrConstants
operator|.
name|JCR_SYSTEM
operator|+
literal|'/'
operator|+
name|REP_NAMESPACES
decl_stmt|;
comment|// TODO: see http://java.net/jira/browse/JSR_333-50)
name|String
name|PREFIX_SV
init|=
literal|"sv"
decl_stmt|;
name|String
name|NAMESPACE_SV
init|=
literal|"http://www.jcp.org/jcr/sv/1.0"
decl_stmt|;
name|String
name|PREFIX_REP
init|=
literal|"rep"
decl_stmt|;
name|String
name|NAMESPACE_REP
init|=
literal|"internal"
decl_stmt|;
comment|// TODO: see OAK-74
comment|// additional XML namespace
name|String
name|PREFIX_XMLNS
init|=
literal|"xmlns"
decl_stmt|;
name|String
name|NAMESPACE_XMLNS
init|=
literal|"http://www.w3.org/2000/xmlns/"
decl_stmt|;
comment|/**      * Reserved namespace prefixes as defined in jackrabbit 2      */
name|Collection
argument_list|<
name|String
argument_list|>
name|RESERVED_PREFIXES
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|NamespaceRegistry
operator|.
name|PREFIX_XML
argument_list|,
name|NamespaceRegistry
operator|.
name|PREFIX_JCR
argument_list|,
name|NamespaceRegistry
operator|.
name|PREFIX_NT
argument_list|,
name|NamespaceRegistry
operator|.
name|PREFIX_MIX
argument_list|,
name|PREFIX_XMLNS
argument_list|,
name|PREFIX_REP
argument_list|,
name|PREFIX_SV
argument_list|)
argument_list|)
decl_stmt|;
comment|/**      * Reserved namespace URIs as defined in jackrabbit 2      */
name|Collection
argument_list|<
name|String
argument_list|>
name|RESERVED_URIS
init|=
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|NamespaceRegistry
operator|.
name|NAMESPACE_XML
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_JCR
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_NT
argument_list|,
name|NamespaceRegistry
operator|.
name|NAMESPACE_MIX
argument_list|,
name|NAMESPACE_XMLNS
argument_list|,
name|NAMESPACE_REP
argument_list|,
name|NAMESPACE_SV
argument_list|)
argument_list|)
decl_stmt|;
comment|// index nodes for faster lookup
name|String
name|REP_NSDATA
init|=
literal|"rep:nsdata"
decl_stmt|;
name|String
name|REP_URIS
init|=
literal|"rep:uris"
decl_stmt|;
name|String
name|REP_PREFIXES
init|=
literal|"rep:prefixes"
decl_stmt|;
block|}
end_interface

end_unit
