begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *   */
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
name|commons
operator|.
name|sort
package|;
end_package

begin_comment
comment|/**  * Source copied from a publicly available library.  * @see<a  *  href="https://code.google.com/p/externalsortinginjava/">https://code.google.com/p/externalsortinginjava</a>  *   * @author Eleftherios Chetzakis  *   */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|StringSizeEstimator
block|{
specifier|private
specifier|static
specifier|final
name|int
name|OBJ_HEADER
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|ARR_HEADER
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|INT_FIELDS
init|=
literal|12
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|OBJ_REF
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|OBJ_OVERHEAD
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|IS_64_BIT_JVM
decl_stmt|;
comment|/**      * Private constructor to prevent instantiation.      */
specifier|private
name|StringSizeEstimator
parameter_list|()
block|{     }
comment|/**      * Class initializations.      */
static|static
block|{
comment|// By default we assume 64 bit JVM
comment|// (defensive approach since we will get
comment|// larger estimations in case we are not sure)
name|boolean
name|is64Bit
init|=
literal|true
decl_stmt|;
comment|// check the system property "sun.arch.data.model"
comment|// not very safe, as it might not work for all JVM implementations
comment|// nevertheless the worst thing that might happen is that the JVM is 32bit
comment|// but we assume its 64bit, so we will be counting a few extra bytes per string object
comment|// no harm done here since this is just an approximation.
name|String
name|arch
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"sun.arch.data.model"
argument_list|)
decl_stmt|;
if|if
condition|(
name|arch
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|arch
operator|.
name|indexOf
argument_list|(
literal|"32"
argument_list|)
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// If exists and is 32 bit then we assume a 32bit JVM
name|is64Bit
operator|=
literal|false
expr_stmt|;
block|}
block|}
name|IS_64_BIT_JVM
operator|=
name|is64Bit
expr_stmt|;
comment|// The sizes below are a bit rough as we don't take into account
comment|// advanced JVM options such as compressed oops
comment|// however if our calculation is not accurate it'll be a bit over
comment|// so there is no danger of an out of memory error because of this.
name|OBJ_HEADER
operator|=
name|IS_64_BIT_JVM
condition|?
literal|16
else|:
literal|8
expr_stmt|;
name|ARR_HEADER
operator|=
name|IS_64_BIT_JVM
condition|?
literal|24
else|:
literal|12
expr_stmt|;
name|OBJ_REF
operator|=
name|IS_64_BIT_JVM
condition|?
literal|8
else|:
literal|4
expr_stmt|;
name|OBJ_OVERHEAD
operator|=
name|OBJ_HEADER
operator|+
name|INT_FIELDS
operator|+
name|OBJ_REF
operator|+
name|ARR_HEADER
expr_stmt|;
block|}
comment|/**      * Estimates the size of a {@link String} object in bytes.      *       * @param s The string to estimate memory footprint.      * @return The<strong>estimated</strong> size in bytes.      */
specifier|public
specifier|static
name|long
name|estimatedSizeOf
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
operator|(
name|s
operator|.
name|length
argument_list|()
operator|*
literal|2
operator|)
operator|+
name|OBJ_OVERHEAD
return|;
block|}
block|}
end_class

end_unit

