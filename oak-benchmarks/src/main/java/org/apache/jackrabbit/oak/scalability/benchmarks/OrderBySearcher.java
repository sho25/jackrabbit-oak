begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing,  *  software distributed under the License is distributed on an  *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  *  KIND, either express or implied.  See the License for the  *  specific language governing permissions and limitations  *  under the License.  */
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
name|scalability
operator|.
name|benchmarks
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
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
name|query
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|query
operator|.
name|QueryManager
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
name|benchmark
operator|.
name|util
operator|.
name|Date
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
name|scalability
operator|.
name|suites
operator|.
name|ScalabilityBlobSearchSuite
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
name|scalability
operator|.
name|suites
operator|.
name|ScalabilityNodeSuite
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
name|scalability
operator|.
name|suites
operator|.
name|ScalabilityAbstractSuite
operator|.
name|ExecutionContext
import|;
end_import

begin_comment
comment|/**  * Searches on path and orders the results by 2 properties  */
end_comment

begin_class
specifier|public
class|class
name|OrderBySearcher
extends|extends
name|PaginationEnabledSearcher
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Override
specifier|protected
name|Query
name|getQuery
parameter_list|(
annotation|@
name|Nonnull
name|QueryManager
name|qm
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|RepositoryException
block|{
comment|// /jcr:root/LongevitySearchAssets/12345//element(*, ParentType) order by @viewed
comment|// descending, @added descending
name|StringBuilder
name|statement
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"/jcr:root/"
argument_list|)
decl_stmt|;
name|statement
operator|.
name|append
argument_list|(
operator|(
operator|(
name|String
operator|)
name|context
operator|.
name|getMap
argument_list|()
operator|.
name|get
argument_list|(
name|ScalabilityBlobSearchSuite
operator|.
name|CTX_ROOT_NODE_NAME_PROP
argument_list|)
operator|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"//element(*, "
argument_list|)
operator|.
name|append
argument_list|(
name|context
operator|.
name|getMap
argument_list|()
operator|.
name|get
argument_list|(
name|ScalabilityNodeSuite
operator|.
name|CTX_ACT_NODE_TYPE_PROP
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getMap
argument_list|()
operator|.
name|containsKey
argument_list|(
name|KEYSET_VAL_PROP
argument_list|)
condition|)
block|{
name|statement
operator|.
name|append
argument_list|(
literal|"[("
argument_list|)
operator|.
name|append
argument_list|(
literal|"@"
argument_list|)
operator|.
name|append
argument_list|(
name|ScalabilityNodeSuite
operator|.
name|CTX_PAGINATION_KEY_PROP
argument_list|)
operator|.
name|append
argument_list|(
literal|"< xs:dateTime('"
argument_list|)
operator|.
name|append
argument_list|(
name|Date
operator|.
name|convertToISO_8601_2000
argument_list|(
operator|(
name|Calendar
operator|)
name|context
operator|.
name|getMap
argument_list|()
operator|.
name|get
argument_list|(
name|KEYSET_VAL_PROP
argument_list|)
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"'))]"
argument_list|)
expr_stmt|;
block|}
name|statement
operator|.
name|append
argument_list|(
name|getOrderByClause
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"{}"
argument_list|,
name|statement
argument_list|)
expr_stmt|;
return|return
name|qm
operator|.
name|createQuery
argument_list|(
name|statement
operator|.
name|toString
argument_list|()
argument_list|,
name|Query
operator|.
name|XPATH
argument_list|)
return|;
block|}
block|}
end_class

end_unit
