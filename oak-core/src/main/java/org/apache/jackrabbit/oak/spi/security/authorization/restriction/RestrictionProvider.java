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
comment|/**  * RestrictionProvider... TODO  */
end_comment

begin_interface
specifier|public
interface|interface
name|RestrictionProvider
block|{
annotation|@
name|Nonnull
name|Set
argument_list|<
name|RestrictionDefinition
argument_list|>
name|getSupportedRestrictions
parameter_list|(
name|String
name|path
parameter_list|)
function_decl|;
name|Restriction
name|createRestriction
parameter_list|(
name|String
name|jcrPath
parameter_list|,
name|String
name|jcrName
parameter_list|,
name|Value
name|value
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
name|Set
argument_list|<
name|Restriction
argument_list|>
name|readRestrictions
parameter_list|(
name|String
name|path
parameter_list|,
name|Tree
name|aceTree
parameter_list|)
throws|throws
name|AccessControlException
function_decl|;
name|void
name|validateRestrictions
parameter_list|(
name|String
name|path
parameter_list|,
name|Tree
name|aceTree
parameter_list|)
throws|throws
name|AccessControlException
function_decl|;
block|}
end_interface

end_unit

