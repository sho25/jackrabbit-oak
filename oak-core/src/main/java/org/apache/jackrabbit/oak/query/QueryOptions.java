begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to You under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law  * or agreed to in writing, software distributed under the License is  * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied. See the License for the specific language  * governing permissions and limitations under the License.  */
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
name|query
package|;
end_package

begin_comment
comment|/**  * A query options (or "hints") that are used to customize the way the query is processed.  */
end_comment

begin_class
specifier|public
class|class
name|QueryOptions
block|{
specifier|public
name|Traversal
name|traversal
init|=
name|Traversal
operator|.
name|DEFAULT
decl_stmt|;
specifier|public
name|String
name|indexName
decl_stmt|;
specifier|public
enum|enum
name|Traversal
block|{
comment|// traversing without index is OK for this query, and does not fail or log a warning
name|OK
block|,
comment|// traversing is OK, but logs a warning
name|WARN
block|,
comment|// traversing will fail the query
name|FAIL
block|,
comment|// the default setting
name|DEFAULT
block|}
empty_stmt|;
block|}
end_class

end_unit

