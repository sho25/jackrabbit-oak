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
name|plugins
operator|.
name|commit
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
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
name|CompositeConflictHandler
import|;
end_import

begin_comment
comment|/**  * Utility class providing conflict handlers used for JCR.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|JcrConflictHandler
block|{
comment|/**      * The conflict handler is a composite of {@link ChildOrderConflictHandler}      * and {@link AnnotatingConflictHandler}.      */
specifier|public
specifier|static
name|CompositeConflictHandler
name|createJcrConflictHandler
parameter_list|()
block|{
return|return
operator|new
name|CompositeConflictHandler
argument_list|(
name|ImmutableList
operator|.
name|of
argument_list|(
operator|new
name|JcrLastModifiedConflictHandler
argument_list|()
argument_list|,
operator|new
name|ChildOrderConflictHandler
argument_list|()
argument_list|,
operator|new
name|AnnotatingConflictHandler
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|JcrConflictHandler
parameter_list|()
block|{     }
block|}
end_class

end_unit

