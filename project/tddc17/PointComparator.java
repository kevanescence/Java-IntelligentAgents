package tddc17;

import java.util.Comparator;

public class PointComparator implements Comparator<Point> {

	@Override
	public int compare(Point arg0, Point arg1) {		
		int tmp = arg0.getCost() - arg1.getCost() ;
		if(tmp < 0)
			return -1;
		if(tmp > 0)
			return 1;
		return 0;
	}

}
