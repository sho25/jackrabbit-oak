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
package|;
end_package

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
name|scalability
operator|.
name|ScalabilityAbstractSuite
operator|.
name|ExecutionContext
import|;
end_import

begin_comment
comment|/**  * Simulates keyset pagination over the search {@link org.apache.jackrabbit.oak.scalability  * .OrderBySearcher}  */
end_comment

begin_class
specifier|public
class|class
name|OrderByKeysetPageSearcher
extends|extends
name|OrderBySearcher
block|{
annotation|@
name|Override
specifier|protected
name|void
name|search
parameter_list|(
name|QueryManager
name|qm
parameter_list|,
name|ExecutionContext
name|context
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|processResultsKeysetPagination
argument_list|(
name|qm
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

