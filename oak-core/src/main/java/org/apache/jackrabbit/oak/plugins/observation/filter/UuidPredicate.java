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
name|observation
operator|.
name|filter
package|;
end_package

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_UUID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
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
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * A predicate for matching against a list of UUIDs. This predicate holds  * whenever the {@code NodeState} passed to its apply functions has a {@code jcr:uuid}  * property and the value of that property matches any of the UUIDs that  * has been passed to the predicate's constructor.  */
end_comment

begin_class
specifier|public
class|class
name|UuidPredicate
implements|implements
name|Predicate
argument_list|<
name|NodeState
argument_list|>
block|{
specifier|private
specifier|final
name|String
index|[]
name|uuids
decl_stmt|;
comment|/**      * @param uuids    uuids      */
specifier|public
name|UuidPredicate
parameter_list|(
annotation|@
name|NotNull
name|String
index|[]
name|uuids
parameter_list|)
block|{
name|this
operator|.
name|uuids
operator|=
name|checkNotNull
argument_list|(
name|uuids
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|test
parameter_list|(
name|NodeState
name|node
parameter_list|)
block|{
if|if
condition|(
name|uuids
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PropertyState
name|uuidProperty
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|JCR_UUID
argument_list|)
decl_stmt|;
if|if
condition|(
name|uuidProperty
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|String
name|parentUuid
init|=
name|uuidProperty
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|uuid
range|:
name|uuids
control|)
block|{
if|if
condition|(
name|parentUuid
operator|.
name|equals
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

