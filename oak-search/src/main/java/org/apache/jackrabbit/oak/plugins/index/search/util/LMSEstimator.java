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
name|search
operator|.
name|util
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
name|query
operator|.
name|Filter
import|;
end_import

begin_comment
comment|/**  * A very simple estimator for no. of entries in the index using least mean square update method for linear regression.  */
end_comment

begin_class
specifier|public
class|class
name|LMSEstimator
block|{
specifier|private
specifier|static
specifier|final
name|double
name|DEFAULT_ALPHA
init|=
literal|0.03
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_THRESHOLD
init|=
literal|5
decl_stmt|;
specifier|private
name|double
index|[]
name|weights
decl_stmt|;
specifier|private
specifier|final
name|double
name|alpha
decl_stmt|;
specifier|private
specifier|final
name|long
name|threshold
decl_stmt|;
specifier|private
name|LMSEstimator
parameter_list|(
name|double
name|alpha
parameter_list|,
name|double
index|[]
name|weights
parameter_list|,
name|long
name|threshold
parameter_list|)
block|{
name|this
operator|.
name|alpha
operator|=
name|alpha
expr_stmt|;
name|this
operator|.
name|weights
operator|=
name|weights
expr_stmt|;
name|this
operator|.
name|threshold
operator|=
name|threshold
expr_stmt|;
block|}
specifier|public
name|LMSEstimator
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_ALPHA
argument_list|,
operator|new
name|double
index|[
literal|5
index|]
argument_list|,
name|DEFAULT_THRESHOLD
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|update
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|long
name|numFound
parameter_list|)
block|{
name|double
index|[]
name|updatedWeights
init|=
operator|new
name|double
index|[
name|weights
operator|.
name|length
index|]
decl_stmt|;
comment|// least mean square cost
name|long
name|estimate
init|=
name|estimate
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|long
name|residual
init|=
name|numFound
operator|-
name|estimate
decl_stmt|;
name|double
name|delta
init|=
name|Math
operator|.
name|pow
argument_list|(
name|residual
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|Math
operator|.
name|abs
argument_list|(
name|delta
argument_list|)
operator|>
name|threshold
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|updatedWeights
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|updatedWeights
index|[
name|i
index|]
operator|=
name|weights
index|[
name|i
index|]
operator|+
name|alpha
operator|*
name|residual
operator|*
name|getInput
argument_list|(
name|filter
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
comment|// weights updated
name|weights
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|updatedWeights
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|long
name|estimate
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|long
name|estimatedEntryCount
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5
condition|;
name|i
operator|++
control|)
block|{
name|estimatedEntryCount
operator|+=
name|weights
index|[
name|i
index|]
operator|*
name|getInput
argument_list|(
name|filter
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|estimatedEntryCount
argument_list|)
operator|||
name|Double
operator|.
name|isNaN
argument_list|(
name|estimatedEntryCount
argument_list|)
condition|)
block|{
comment|// prevent over / under flow
name|estimatedEntryCount
operator|=
literal|1
expr_stmt|;
name|weights
operator|=
operator|new
name|double
index|[
literal|5
index|]
expr_stmt|;
block|}
return|return
name|Math
operator|.
name|max
argument_list|(
literal|0
argument_list|,
name|estimatedEntryCount
argument_list|)
return|;
block|}
comment|/**      * Get the input value for a certain feature (by index) in the given filter.      *<p/>      * A filter is represented as a vector in R^5 where      * i_0 : no. of property restrictions      * i_1 : 1 if any native constraint exists in the filter, 0 otherwise      * i_2 : the path restriction ordinal      * i_3 : the depth of the path restriction if set, 0 otherwise      * i_4 : the precedence of the dominant full text constraint if present, 0 otherwise      *      * @param filter the filter      * @param i      the index of the filter vector feature to retrieve      * @return the feature value      */
specifier|private
name|long
name|getInput
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|int
name|i
parameter_list|)
block|{
assert|assert
name|i
operator|<
literal|5
assert|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
return|return
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
operator|!=
literal|null
condition|?
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
operator|.
name|size
argument_list|()
else|:
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
literal|1
condition|)
block|{
return|return
name|filter
operator|.
name|containsNativeConstraint
argument_list|()
condition|?
literal|1
else|:
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
literal|2
condition|)
block|{
return|return
name|filter
operator|.
name|getPathRestriction
argument_list|()
operator|!=
literal|null
condition|?
name|filter
operator|.
name|getPathRestriction
argument_list|()
operator|.
name|ordinal
argument_list|()
else|:
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
literal|3
condition|)
block|{
return|return
name|filter
operator|.
name|getPathRestriction
argument_list|()
operator|!=
literal|null
condition|?
name|filter
operator|.
name|getPathRestriction
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
operator|.
name|length
else|:
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
literal|4
condition|)
block|{
return|return
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
operator|!=
literal|null
condition|?
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
operator|.
name|getPrecedence
argument_list|()
else|:
literal|0
return|;
block|}
return|return
literal|0
return|;
block|}
block|}
end_class

end_unit

