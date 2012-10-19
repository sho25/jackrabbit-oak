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
name|value
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|io
operator|.
name|ByteStreams
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
name|Blob
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
name|plugins
operator|.
name|memory
operator|.
name|StringBasedBlob
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
name|util
operator|.
name|ISO8601
import|;
end_import

begin_comment
comment|/**  * Utility class defining the conversion that take place between {@link org.apache.jackrabbit.oak.api.PropertyState}s  * of different types. All conversions defined in this class are compatible with the conversions specified  * in JSR-283 $3.6.4. However, some conversion in this class might not be defined in JSR-283.  *<p>  * Example:  *<pre>  *    double three = convert("3.0").toDouble();  *</pre>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Conversions
block|{
specifier|private
name|Conversions
parameter_list|()
block|{}
comment|/**      * A converter converts a value to its representation as a specific target type. Not all target      * types might be supported for a given value in which case implementations throw an exception.      * The default implementations of the various conversion methods all operate on the string      * representation of the underlying value (i.e. call {@code Converter.toString()}.      */
specifier|public
specifier|abstract
specifier|static
class|class
name|Converter
block|{
comment|/**          * Convert to string          * @return  string representation of the converted value          */
specifier|public
specifier|abstract
name|String
name|toString
parameter_list|()
function_decl|;
comment|/**          * Convert to binary. This default implementation returns an new instance          * of {@link StringBasedBlob}.          * @return  binary representation of the converted value          */
specifier|public
name|Blob
name|toBinary
parameter_list|()
block|{
return|return
operator|new
name|StringBasedBlob
argument_list|(
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * Convert to long. This default implementation is based on {@code Long.parseLong(String)}.          * @return  long representation of the converted value          * @throws NumberFormatException          */
specifier|public
name|long
name|toLong
parameter_list|()
block|{
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * Convert to double. This default implementation is based on {@code Double.parseDouble(String)}.          * @return  double representation of the converted value          * @throws NumberFormatException          */
specifier|public
name|double
name|toDouble
parameter_list|()
block|{
return|return
name|Double
operator|.
name|parseDouble
argument_list|(
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * Convert to date. This default implementation is based on {@code ISO8601.parse(String)}.          * @return  date representation of the converted value          * @throws IllegalArgumentException  if the string cannot be parsed into a date          */
specifier|public
name|Calendar
name|toCalendar
parameter_list|()
block|{
name|Calendar
name|date
init|=
name|ISO8601
operator|.
name|parse
argument_list|(
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|date
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Not a date string: "
operator|+
name|toString
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|date
return|;
block|}
comment|/**          * Convert to date. This default implementation is based on {@code ISO8601.parse(String)}.          * @return  date representation of the converted value          * @throws IllegalArgumentException  if the string cannot be parsed into a date          */
specifier|public
name|String
name|toDate
parameter_list|()
block|{
return|return
name|convert
argument_list|(
name|toCalendar
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**          * Convert to boolean. This default implementation is based on {@code Boolean.parseBoolean(String)}.          * @return  boolean representation of the converted value          */
specifier|public
name|boolean
name|toBoolean
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|toString
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * Convert to decimal. This default implementation is based on {@code new BigDecimal(String)}.          * @return  decimal representation of the converted value          * @throws NumberFormatException          */
specifier|public
name|BigDecimal
name|toDecimal
parameter_list|()
block|{
return|return
operator|new
name|BigDecimal
argument_list|(
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**      * Create a converter for a string.      * @param value  The string to convert      * @return  A converter for {@code value}      * @throws NumberFormatException      */
specifier|public
specifier|static
name|Converter
name|convert
parameter_list|(
specifier|final
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|Converter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
return|;
block|}
comment|/**      * Create a converter for a binary.      * For the conversion to {@code String} the binary in interpreted as UTF-8 encoded string.      * @param value  The binary to convert      * @return  A converter for {@code value}      * @throws IllegalArgumentException  if the binary is inaccessible      */
specifier|public
specifier|static
name|Converter
name|convert
parameter_list|(
specifier|final
name|Blob
name|value
parameter_list|)
block|{
return|return
operator|new
name|Converter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
try|try
block|{
name|InputStream
name|in
init|=
name|value
operator|.
name|getNewStream
argument_list|()
decl_stmt|;
try|try
block|{
return|return
operator|new
name|String
argument_list|(
name|ByteStreams
operator|.
name|toByteArray
argument_list|(
name|in
argument_list|)
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
return|;
block|}
finally|finally
block|{
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Blob
name|toBinary
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
return|;
block|}
comment|/**      * Create a converter for a long. {@code String.valueOf(long)} is used for the conversion to {@code String}.      * The conversions to {@code double} and {@code long} return the {@code value} itself.      * The conversion to decimal uses {@code new BigDecimal.valueOf(long)}.      * The conversion to date interprets the value as number of milliseconds since {@code 1970-01-01T00:00:00.000Z}.      * @param value  The long to convert      * @return  A converter for {@code value}      */
specifier|public
specifier|static
name|Converter
name|convert
parameter_list|(
specifier|final
name|long
name|value
parameter_list|)
block|{
return|return
operator|new
name|Converter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toLong
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|toDouble
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|Calendar
name|toCalendar
parameter_list|()
block|{
name|Calendar
name|date
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT+00:00"
argument_list|)
argument_list|)
decl_stmt|;
name|date
operator|.
name|setTimeInMillis
argument_list|(
name|value
argument_list|)
expr_stmt|;
return|return
name|date
return|;
block|}
annotation|@
name|Override
specifier|public
name|BigDecimal
name|toDecimal
parameter_list|()
block|{
return|return
name|BigDecimal
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * Create a converter for a double. {@code String.valueOf(double)} is used for the conversion to {@code String}.      * The conversions to {@code double} and {@code long} return the {@code value} itself where in the former case      * the value is casted to {@code long}.      * The conversion to decimal uses {@code BigDecimal.valueOf(double)}.      * The conversion to date interprets {@code toLong()} as number of milliseconds since      * {@code 1970-01-01T00:00:00.000Z}.      * @param value  The double to convert      * @return  A converter for {@code value}      */
specifier|public
specifier|static
name|Converter
name|convert
parameter_list|(
specifier|final
name|double
name|value
parameter_list|)
block|{
return|return
operator|new
name|Converter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toLong
parameter_list|()
block|{
return|return
operator|(
name|long
operator|)
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|toDouble
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|Calendar
name|toCalendar
parameter_list|()
block|{
name|Calendar
name|date
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT+00:00"
argument_list|)
argument_list|)
decl_stmt|;
name|date
operator|.
name|setTimeInMillis
argument_list|(
name|toLong
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|date
return|;
block|}
annotation|@
name|Override
specifier|public
name|BigDecimal
name|toDecimal
parameter_list|()
block|{
return|return
name|BigDecimal
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * Create a converter for a date. {@code ISO8601.format(Calendar)} is used for the conversion to {@code String}.      * The conversions to {@code double}, {@code long} and {@code BigDecimal} return the number of milliseconds      * since  {@code 1970-01-01T00:00:00.000Z}.      * @param value  The date to convert      * @return  A converter for {@code value}      */
specifier|public
specifier|static
name|Converter
name|convert
parameter_list|(
specifier|final
name|Calendar
name|value
parameter_list|)
block|{
return|return
operator|new
name|Converter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|ISO8601
operator|.
name|format
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toLong
parameter_list|()
block|{
return|return
name|value
operator|.
name|getTimeInMillis
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|toDouble
parameter_list|()
block|{
return|return
name|value
operator|.
name|getTimeInMillis
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Calendar
name|toCalendar
parameter_list|()
block|{
return|return
name|value
return|;
block|}
annotation|@
name|Override
specifier|public
name|BigDecimal
name|toDecimal
parameter_list|()
block|{
return|return
operator|new
name|BigDecimal
argument_list|(
name|value
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
comment|/**      * Create a converter for a boolean. {@code Boolean.toString(boolean)} is used for the conversion to {@code String}.      * @param value  The boolean to convert      * @return  A converter for {@code value}      */
specifier|public
specifier|static
name|Converter
name|convert
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
return|return
operator|new
name|Converter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|toBoolean
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
return|;
block|}
comment|/**      * Create a converter for a decimal. {@code BigDecimal.toString()} is used for the conversion to {@code String}.      * {@code BigDecimal.longValue()} and {@code BigDecimal.doubleValue()} is used for the conversions to      * {@code long} and {@code double}, respectively.      * The conversion to date interprets {@code toLong()} as number of milliseconds since      * {@code 1970-01-01T00:00:00.000Z}.      * @param value  The decimal to convert      * @return  A converter for {@code value}      */
specifier|public
specifier|static
name|Converter
name|convert
parameter_list|(
specifier|final
name|BigDecimal
name|value
parameter_list|)
block|{
return|return
operator|new
name|Converter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|toLong
parameter_list|()
block|{
return|return
name|value
operator|.
name|longValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|toDouble
parameter_list|()
block|{
return|return
name|value
operator|.
name|doubleValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Calendar
name|toCalendar
parameter_list|()
block|{
name|Calendar
name|date
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT+00:00"
argument_list|)
argument_list|)
decl_stmt|;
name|date
operator|.
name|setTimeInMillis
argument_list|(
name|toLong
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|date
return|;
block|}
annotation|@
name|Override
specifier|public
name|BigDecimal
name|toDecimal
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

