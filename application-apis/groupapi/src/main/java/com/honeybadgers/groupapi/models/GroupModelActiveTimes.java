package com.honeybadgers.groupapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.honeybadgers.models.ActiveTimes;
import io.swagger.annotations.ApiModelProperty;

import java.sql.Time;
import java.util.Objects;

public class GroupModelActiveTimes {
    @JsonProperty("to")
    private Time to;

    @JsonProperty("from")
    private Time from;

    public GroupModelActiveTimes to(Time to) {
        this.to = to;
        return this;
    }

    /**
     * Get to
     * @return to
     */
    @ApiModelProperty(value = "")


    public Time getTo() {
        return to;
    }

    public void setTo(Time to) {
        this.to = to;
    }

    public GroupModelActiveTimes from(Time from) {
        this.from = from;
        return this;
    }

    /**
     * Get from
     * @return from
     */
    @ApiModelProperty(value = "")


    public Time getFrom() {
        return from;
    }

    public void setFrom(Time from) {
        this.from = from;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GroupModelActiveTimes taskModelActiveTimes = (GroupModelActiveTimes) o;
        return Objects.equals(this.to, taskModelActiveTimes.to) &&
                Objects.equals(this.from, taskModelActiveTimes.from);
    }

    @Override
    public int hashCode() {
        return Objects.hash(to, from);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class GroupModelActiveTimes {\n");

        sb.append("    to: ").append(toIndentedString(to)).append("\n");
        sb.append("    from: ").append(toIndentedString(from)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }



    public ActiveTimes getAsJpaModel() {
        ActiveTimes active = new ActiveTimes();
        active.setFrom(this.from);
        active.setTo(this.to);
        return active;
    }
}
