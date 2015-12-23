/*
 * Copyright 2015 Paul Gestwicki
 *
 * This file is part of The Social Startup Game
 *
 * The Social Startup Game is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Social Startup Game is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with The Social Startup Game.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.bsu.cybersec.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import edu.bsu.cybersec.core.ui.GameAssets;
import playn.core.Image;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class EmployeePool {

    private static final Random RANDOM = new Random();

    private static ImmutableMap<EmployeeProfile, Image> makeDeveloperMap(GameAssets assets) {
        ImmutableMap.Builder<EmployeeProfile, Image> builder = ImmutableMap.builder();
        builder.put(EmployeeProfile.firstName("Esteban").lastName("Cortez")
                        .withDegree("Bachelors in Computer Science").from("Ball State University")
                        .bio("Esteban worked in a factory until he was 33, then he went to college and decided to get involved in software development."),
                assets.getImage(GameAssets.ImageKey.ESTEBAN));
        builder.put(EmployeeProfile.firstName("Nancy").lastName("Stevens")
                        .withDegree("Bachelors in English").from("Georgetown University")
                        .withDegree("Masters in Computer Security").from("Purdue University")
                        .bio("Nancy has a popular podcast about being a woman in technology."),
                assets.getImage(GameAssets.ImageKey.NANCY));
        builder.put(EmployeeProfile.firstName("Jerry").lastName("Chen")
                        .withDegree("Bachelors in Computer Science").from("University of Hong Kong")
                        .bio("Jerry interned at a local company in high school and has been working as a software developer ever since."),
                assets.getImage(GameAssets.ImageKey.JERRY));
        builder.put(EmployeeProfile.firstName("Vani").lastName("Mishra")
                        .withDegree("Bachelors in Computer Engineering").from("Indian Institute of Science")
                        .withDegree("Masters in Software Engineering").from("Ball State University")
                        .bio("Vani was born in India and came to the United States for graduate school. She loves music, dancing, and PHP."),
                assets.getImage(GameAssets.ImageKey.VANI));
        builder.put(EmployeeProfile.firstName("Abdullah").lastName("Nasr")
                        .withDegree("Bachelors in Electrical Engineering").from("Iowa State University")
                        .bio("Abdullah used to work for a larger social media company, but he prefers the excitement of a small startup."),
                assets.getImage(GameAssets.ImageKey.ABDULLAH));
        builder.put(EmployeeProfile.firstName("Janine").lastName("Palmer")
                        .withDegree("Bachelors in Computer Science").from("Virginia Tech")
                        .bio("Janine is especially talented at meeting with customers and understanding what they want from a product."),
                assets.getImage(GameAssets.ImageKey.JANINE));
        return builder.build();
    }

    public static EmployeePool create(GameAssets cache) {
        return new EmployeePool(cache);
    }

    private final GameAssets cache;

    private EmployeePool(GameAssets cache) {
        this.cache = checkNotNull(cache);
    }

    public Set<Employee> recruit(int numberOfEmployees) {
        checkArgument(numberOfEmployees >= 0);
        Map<EmployeeProfile, Image> map = makeDeveloperMap(cache);
        Set<Employee> result = Sets.newHashSet();
        List<EmployeeProfile> employeePool = Lists.newLinkedList(map.keySet());
        for (int i = 0; i < numberOfEmployees; i++) {
            EmployeeProfile profile = employeePool.remove(RANDOM.nextInt(employeePool.size()));
            result.add(new Employee(profile, map.get(profile)));
        }
        return result;
    }

    public Employee removeOne() {
        return recruit(1).iterator().next();
    }

}
